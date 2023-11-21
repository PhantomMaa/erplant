package io.erplant

import java.util.regex.Pattern

interface Parser {

    fun parse(content: String): List<Database>
}

class LineParser : Parser {

    private val defaultPattern: Pattern = Pattern.compile("(default\\(['\"\\w]+\\))")
    private val varcharPattern: Pattern = Pattern.compile("(varchar\\(\\d+\\))")
    private val regexBracket = Regex("(?<=\\()(.+?)(?=\\))")

    private fun isDatabaseLine(str: String) = str.startsWith("Database(") && str.endsWith("{")

    private fun isTableLine(str: String) = str.startsWith("Table(") && str.endsWith("{")

    /**
     * 裁剪匹配行之前的内容
     */
    private fun cutHead(allLines: List<String>, predicate: (str: String) -> Boolean): List<String> {
        var index = 0L
        for (i in allLines.indices) {
            val trimLine = allLines[i].trim()
            if (predicate(trimLine)) {
                index = i.toLong()
                break
            }
        }

        return allLines.drop(index.toInt())
    }

    /**
     * 裁掉"}"之后的内容，可以指定是否保留最后一个"}"
     */
    private fun cutTail(allLines: List<String>, retainTail: Boolean): List<String> {
        var endIndex = 0L
        allLines.forEachIndexed { i, line ->
            run {
                val trimLine = line.trim()
                if (trimLine.endsWith("}")) {
                    endIndex = i.toLong()
                }
            }
        }
        endIndex = if (retainTail) endIndex + 1 else endIndex
        return allLines.take(endIndex.toInt())
    }

    /**
     * 多行字符串，按照行固定开头的一行，拆分为多个段落
     */
    private fun splitByStartedLine(lines: List<String>, predicate: (str: String) -> Boolean): List<List<String>> {
        val sectionList = mutableListOf<List<String>>()

        // 计算有几个分段
        val count = lines.stream().filter(predicate).count()

        // 分割多个段落，将内容逆序，按照匹配到开头的行 切割为多个段落
        var reversedLines = lines.reversed()
        for (index in 0 until count) {
            reversedLines = receiveFirstMatched(reversedLines, sectionList, predicate)
        }

        return sectionList.reversed()
    }

    /**
     * 多行字符串，按照行匹配的方式，拆分为多个段落
     */
    private fun splitByMatchedLine(lines: List<String>, predicate: (str: String) -> Boolean): List<List<String>> {
        val sectionList = mutableListOf<List<String>>()
        // 保存所有的分隔位置的index
        val splitIndexs = mutableListOf<Int>()
        for (i in lines.indices) {
            val line = lines[i]
            val trimLine = line.trim()
            if (predicate(trimLine)) {
                splitIndexs.add(i)
            }
        }
        splitIndexs.add(lines.size)

        var lastIndex = 0
        for (i: Int in splitIndexs) {
            // 跳过分隔的那行
            val tempIndex = if (lastIndex == 0) 0 else lastIndex + 1
            val subList = lines.subList(tempIndex, i)
            sectionList.add(subList)
            lastIndex = i
        }
        return sectionList
    }

    /**
     * 将不断裁剪的字符串内容，每遇到一个标记段（可以是Database或Table），把它保留到sectionList
     */
    private fun receiveFirstMatched(
        lines: List<String>,
        sectionList: MutableList<List<String>>,
        predicate: (str: String) -> Boolean
    ): List<String> {
        var index = 0
        for (i in lines.indices) {
            val line = lines[i]
            val trimLine = line.trim()
            if (predicate(trimLine)) {
                index = i
                break
            }
        }

        // 如果是最后一段，将index置为结尾位置
        index = if (index == 0) lines.size else index
        val matched = lines.take(index + 1).reversed()
        sectionList.add(matched)

        // 跳过已保存下来的段落，返回剩下的，作为下次的输入
        return lines.drop(index + 1)
    }

    private fun parseDatabase(allLines: List<String>): Database {
        // 从第一行解析出来Database名
        val databaseName = allLines[0].split("Database(", ",", ")")[1]

        // 去掉头部，和Table无关的内容
        val cutHeadLines = cutHead(allLines) { isTableLine(it.trim()) }

        // 去掉尾部，以及取消Table段落的一层缩进
        val lines = cutTail(cutHeadLines, false).map {
            if (it.startsWith(StrConstant.INDENT)) it.substring(StrConstant.INDENT.length) else it
        }

        // 以Table开头为分隔界限，拆分为多个段落
        val tbSectionList: List<List<String>> = splitByStartedLine(lines) { isTableLine(it.trim()) }

        // 将字符串段落，转成Table对象的列表
        val tables = tbSectionList.map { parseTable(databaseName, it) }
        return Database(databaseName, tables)
    }

    private fun parseTable(databaseName: String, allLines: List<String>): Table {
        // 从第一行解析出来表名、表注释
        val tableLineSplits = allLines[0].split("(", ",", ")")
        val tableName = tableLineSplits[1].trim()
        val doName = tableLineSplits[2].trim()
        val tableComment = tableLineSplits[3].trim()

        // 去掉头部、尾部，去掉注释的行，以及取消字段段落的一层缩进
        val lines = allLines
            .take(allLines.size - 1).drop(1)
            .filter { !it.startsWith("'") }
            .map { if (it.startsWith(StrConstant.INDENT)) it.substring(StrConstant.INDENT.length) else it }

        val secList = splitByMatchedLine(lines) { it.startsWith("--") }
        val columnSection = secList[0].toMutableList()
        val indexSection = secList[1].toMutableList()

        val columns = columnSection.filter { it.isNotBlank() }.map { parseColumn(it) }
        val columnMap = columns.associateBy { it.columnName }
        val indexs = indexSection.filter { it.isNotBlank() }.mapNotNull { parseIndex(it, columnMap) }

        val extensionSection = if (secList.size > 2) secList[2] else emptyList()
        val extensions = extensionSection.filter { it.isNotBlank() }.mapNotNull { parseExtension(it) }
        return Table(databaseName, tableName, doName, tableComment, columns, indexs, extensions)
    }

    private fun parseColumn(columnLine: String): Column {
        val columnSplit = columnLine.trim().split(" ")
        val columnName = columnSplit[0]
        val type: String
        var length: Int? = null
        val typeStr = columnSplit[1]
        if (typeStr.startsWith(StrConstant.STR_VARCHAR)) {
            type = StrConstant.STR_VARCHAR
            val varcharMatcher = varcharPattern.matcher(typeStr)
            if (varcharMatcher.find()) {
                val lengthStr = varcharMatcher.group()
                length = lengthStr.split("(", ")")[1].toInt()
            }
        } else {
            type = typeStr
        }
        val notNull = columnLine.contains(StrConstant.NOT_NULL)
        val autoIncrement = columnLine.contains(ParseRuleConstant.AUTO_INCREMENT)
        val autoTimestamp = columnLine.contains(ParseRuleConstant.AUTO_TIMESTAMP)
        val likeQuery = columnLine.contains(ParseRuleConstant.LIKE_QUERY)
        var defaultValue: String? = null
        val defaultMatcher = defaultPattern.matcher(columnLine)
        if (defaultMatcher.find()) {
            val defaultStr = defaultMatcher.group()
            defaultValue = defaultStr.split("(", ")")[1]
        }

        val comment: String? = parseComment(columnLine)
        // when是一个if else的写法，匹配到即返回
        val columnType = when {
            columnLine.contains(ParseRuleConstant.ENTITY_ID) -> {
                // PrimaryID有更高的优先级
                ColumnType.PrimaryID
            }

            columnLine.contains(StrConstant.STR_PRIMARY_KEY) -> {
                ColumnType.Primary
            }

            else -> {
                ColumnType.Normal
            }
        }
        return Column(
            columnName,
            type,
            length,
            autoIncrement,
            notNull,
            defaultValue,
            comment,
            columnType,
            autoTimestamp,
            likeQuery
        )
    }

    private fun parseIndex(indexLine: String, columnMap: Map<String, Column>): Index? {
        when {
            indexLine.contains(StrConstant.STR_UNIQUE_KEY) -> {
                val indexName = indexLine.split(" ")[2]
                val columnNames = indexLine.split("(", ")")[1]
                val columnList = columnNames.split(",")
                    .mapNotNull { columnMap[it.trim()] }
                    .map {
                        if (it.columnType == ColumnType.Normal) {
                            it.columnType = ColumnType.UniqueKey
                        }
                        it
                    }

                // UniqueBK是Unique的一种特殊情况，有更高的优先级
                val indexType = if (indexLine.contains(ParseRuleConstant.BUSSINESS_KEY))
                    IndexType.UniqueBK
                else
                    IndexType.Unique
                return Index(indexName, indexType, columnList)
            }

            indexLine.contains(StrConstant.STR_INDEX) -> {
                val indexName = indexLine.split(" ")[1]
                val columnNames = indexLine.split("(", ")")[1]
                val columnList = columnNames.split(",")
                    .mapNotNull { columnMap[it.trim()] }
                    .map {
                        it.columnType = ColumnType.Index
                        it
                    }

                return Index(indexName, IndexType.Index, columnList)
            }

            else -> {
                println("parseIndex can not match the index types, indexStr : $indexLine")
                return null
            }
        }
    }

    private fun parseExtension(extensionStr: String): Extension? {
        val split = extensionStr.split(" ")
        val nameSplit = split[0].split(".")
        if (nameSplit.size != 2) {
            println("extension attribute must has column name prefix, extensionStr : $extensionStr")
            return null
        }

        val columnName = nameSplit[0]
        val fieldName = nameSplit[1]
        val fieldType = split[1]
        val comment: String? = parseComment(extensionStr)
        return Extension(columnName, fieldName, fieldType, comment)
    }

    private fun parseComment(line: String): String? {
        val splitStr = "comment("
        val startIndex = line.indexOf(splitStr)
        if (startIndex < 0) {
            return null
        }

        return regexBracket.findAll(line).last().value
    }

    private fun preProcess(allLines: List<String>, predicate: (str: String) -> Boolean): List<String> {
        // 去掉头部无关的内容，去除空行
        val cutHeadLines = cutHead(allLines.filter { it.isNotBlank() }, predicate)
        // 去掉尾部无关的内容
        return cutTail(cutHeadLines, true)
    }

    override fun parse(content: String): List<Database> {
        val allLines = content.split("\n")

        // 判断是否有Database层
        val hasDatabase = allLines.stream().anyMatch { isDatabaseLine(it) }
        val sectionList: List<List<String>> = if (hasDatabase) {
            // 对输入内容预处理，去头、去尾、去除空行
            val lines = preProcess(allLines) { isDatabaseLine(it) }
            // 以Database开头为分隔界限，拆分为多个段落
            splitByStartedLine(lines) { isDatabaseLine(it) }
        } else {
            // 对输入内容预处理，去头、去尾、去除空行
            val lines = preProcess(allLines) { isTableLine(it) }
            // 以Table开头为分隔界限，拆分为多个段落
            splitByStartedLine(lines) { isTableLine(it) }
        }

        // 将多段内容转为Database对象
        return sectionList.map { parseDatabase(it) }
    }

}