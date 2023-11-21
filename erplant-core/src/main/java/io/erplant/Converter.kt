package io.erplant

import io.erplant.utils.HumpUtil

abstract class Converter(val tableConfigMap: Map<String, TableConfig>) {

    /**
     * 为db下的每张表生成一份转换结果
     */
    abstract fun convert(database: Database): List<FileContent>

    /**
     * 返回convert负责转换的类型
     */
    abstract fun getSupportType(): SupportType

    /**
     * 返回结果文件的输出路径
     */
    abstract fun getOutputDirectory(databaseName: String): String

}

/**
 * 为每个表单独生成转换结果的converter
 */
abstract class TableConverter(tableConfigMap: Map<String, TableConfig>) : Converter(tableConfigMap) {

    override fun convert(database: Database): List<FileContent> {
        return database.tableList.mapNotNull {
            val enableThisConverter = tableConfigMap[it.tableName] == null || tableConfigMap[it.tableName]!!.generateTypeSet().contains(this.getSupportType().name)
            if (enableThisConverter) {
                convertTable(database.databaseName, it)
            } else {
                null
            }
        }
    }

    /**
     * 为db下的每张表生成一份转换结果
     */
    abstract fun convertTable(databaseName: String, table: Table): FileContent?

    /**
     * 获得每个表对应的输出结果的文件名
     */
    abstract fun getOutFileName(tableName: String): String

    /**
     * 是否支持批量查询
     */
    fun isSupportBatchGet(table: Table): Boolean {
        return table.getPrimaryIDColumn() != null
    }
}

abstract class JavaConverter(val packageName: String, tableConfigMap: Map<String, TableConfig>) :
    TableConverter(tableConfigMap) {

    val collectionTypes = listOf("List", "Set", "Map")

    private val typeMap = mapOf(
        "boolean" to "Boolean",
        "tinyint" to "Integer",
        "tinyint_unsigned" to "Integer",
        "smallint" to "Integer",
        "smallint_unsigned" to "Integer",
        "int" to "Integer",
        "int_unsigned" to "Long",
        "bigint" to "Long",
        "bigint_unsigned" to "Long",
        "decimal" to "Double",
        "date" to "Instant",
        "datetime" to "Instant",
        "timestamp" to "Instant",
        "blob" to "byte[]",
        "text" to "String",
        "json" to "String",
        "varchar" to "String"
    )

    fun convertToFieldType(column: Column): String {
        return typeMap[column.type] ?: "String"
    }

    /**
     * 获得每个表对应的输出结果的文件名
     */
    override fun getOutFileName(tableName: String): String {
        return HumpUtil.lineToHump(tableName, true) + "${getSupportType().name}.java"
    }

    /**
     * 返回结果文件的输出路径
     */
    override fun getOutputDirectory(databaseName: String): String {
        val converterDirectory = this.getSupportType().directory
        val javaPackageName = HumpUtil.lineToHump(databaseName, false).lowercase()
        return "/$packageName/$javaPackageName/$converterDirectory".replace(".", "/")
    }

    fun columnToField(column: Column): Field {
        return Field(
            fieldName = HumpUtil.lineToHump(column.columnName, false),
            fileType = convertToFieldType(column),
            column.comment,
            column.notNull
        )
    }

}
