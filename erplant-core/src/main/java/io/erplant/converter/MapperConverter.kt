package io.erplant.converter

import io.erplant.*
import io.erplant.utils.HumpUtil

class MapperConverter(private val packageName: String, private val render: VelocityRender,
    tableConfigMap: Map<String, TableConfig>) : TableConverter(tableConfigMap) {

    override fun getSupportType(): SupportType {
        return SupportType.Mapper
    }

    override fun getOutputDirectory(databaseName: String): String {
        return this.getSupportType().directory + "/" + databaseName
    }

    /**
     * 获得每个表对应的输出结果的文件名
     */
    override fun getOutFileName(tableName: String): String {
        return HumpUtil.humpToLine(tableName) + "-${getSupportType().suffix}.xml"
    }

    private fun columnsToXmlColumns(columns: List<Column>): List<XmlColumn> {
        return columns.map {
            XmlColumn(
                it.columnName,
                columnValueName = HumpUtil.lineToHump(it.columnName, false),
                it.columnType,
                notNullQueryField = false,
                autoIncrement = it.autoIncrement,
                likeQuery = false
            )
        }
    }

    override fun convertTable(databaseName: String, table: Table): FileContent {
        val columns: List<XmlColumn> = columnsToXmlColumns(table.columns)
        val indexes = table.indexes ?: emptyList()
        val primaryColumn = columns.first { it.columnName == table.getPrimaryColumn().columnName }
        val bkColumns = columnsToXmlColumns(table.getBkColumns() ?: emptyList())
        val skipUpdateColumnNames = SkipUpdateColumn.values().map { it.name.lowercase() }.toSet()
        val insertColumns = columns.filter { !skipUpdateColumnNames.contains(it.columnName) }
        val updateColumns = columns
            .filter { it.columnType != ColumnType.Primary && it.columnType != ColumnType.PrimaryID }
            .filter { !skipUpdateColumnNames.contains(it.columnName) }

        val queryWhereColumns = genQueryWhereColumns(table, indexes)
        val tableInfo = TableInfo(packageName, databaseName, table, render)
        val supportGetByBk = table.indexes?.any { it.indexType == IndexType.UniqueBK } ?: false
        val supportBatchGet = super.isSupportBatchGet(table)
        val getByUkSections = indexes.filter { it.indexType == IndexType.Unique }
            .map { genGetByUkSection(it) }

        val mapperXml = MapperXml(
            tableInfo,
            primaryColumn,
            bkColumns,
            insertColumns,
            updateColumns,
            queryWhereColumns,
            supportGetByBk,
            supportBatchGet,
            table.needGenPageNumQuery(),
            table.needGenOffsetQuery(),
            getByUkSections
        )
        val output = render.output(SupportType.Mapper.name, mapperXml)
        val fileName = getOutFileName(table.doName)
        return FileContent(fileName, output)
    }

    private fun genGetByUkSection(ukIndex: Index): GetByUkSection {
        val whereColumns = ukIndex.columns.map {
            XmlColumn(
                it.columnName,
                columnValueName = HumpUtil.lineToHump(it.columnName, false),
                it.columnType,
                notNullQueryField = true,
                autoIncrement = it.autoIncrement,
                likeQuery = false
            )
        }
        val bigUkName = HumpUtil.lineToHump(ukIndex.indexName.split("uk_")[1], true)
        return GetByUkSection(bigUkName, whereColumns)
    }

    private fun genQueryWhereColumns(table: Table, indexes: List<Index>): List<XmlColumn> {
        if (indexes.isEmpty()) {
            return emptyList()
        }

        val queryColumns = table.getQueryColumns() ?: return emptyList()
        return queryColumns.map {
            XmlColumn(
                it.columnName,
                columnValueName = HumpUtil.lineToHump(it.columnName, false),
                it.columnType,
                notNullQueryField = false,
                autoIncrement = it.autoIncrement,
                it.likeQuery
            )
        }
    }
}