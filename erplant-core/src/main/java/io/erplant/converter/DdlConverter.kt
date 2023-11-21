package io.erplant.converter

import io.erplant.*

class DdlConverter(private val packageName: String, private val render: VelocityRender) : Converter(emptyMap()) {

    private val type = SupportType.Ddl

    override fun convert(database: Database): List<FileContent> {
        return database.tableList.map { convertTable(database.databaseName, it) }
    }

    override fun getSupportType(): SupportType {
        return type
    }

    override fun getOutputDirectory(databaseName: String): String {
        return type.directory
    }

    private fun convertTable(databaseName: String, table: Table): FileContent {
        val columns = table.columns.map { convertColumn(it) }
        var indexes: List<DdlIndex>? = null
        if (table.indexes != null) {
            indexes = table.indexes.map { convertIndex(it) }
        }
        val tableInfo = TableInfo(packageName, databaseName, table, render)
        val ddlTable = DdlTable(tableInfo, columns, indexes)
        val output = render.output(type.name, ddlTable)
        return FileContent(table.tableName, output)
    }

    private fun convertColumn(column: Column): DdlColumn {
        val columnType = if (column.length != null) {
            "${column.type}(${column.length})"
        } else {
            if (column.type.endsWith("_unsigned")) {
                column.type.split("_").joinToString(separator = " ") { it }
            } else {
                column.type
            }
        }
        val notNull = if (column.notNull) "NOT NULL" else null
        val defaultValue = if (column.defaultValue != null) "DEFAULT " + column.defaultValue else null
        val autoIncrement = if (column.autoIncrement) "AUTO_INCREMENT" else null
        val isPrimary = column.columnType == ColumnType.PrimaryID || column.columnType == ColumnType.Primary
        return DdlColumn(
            column.columnName,
            columnType,
            notNull,
            defaultValue,
            isPrimary,
            autoIncrement,
            column.autoTimestamp
        )
    }

    private fun convertIndex(index: Index): DdlIndex {
        val indexType: String = when (index.indexType) {
            IndexType.UniqueBK -> {
                "UNIQUE KEY"
            }

            IndexType.Unique -> {
                "UNIQUE KEY"
            }

            IndexType.Index -> {
                "INDEX"
            }
        }
        val columnNames = index.columns
            .joinToString(separator = ", ", prefix = "(", postfix = ")") { "`${it.columnName}`" }
        return DdlIndex(type = indexType, name = index.indexName, columnNames = columnNames)
    }
}