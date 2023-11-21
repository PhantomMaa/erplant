package io.erplant

// 以下是erplant程序理解的类型
data class FileContent(val fileName: String, val content: String)

data class Database(
    val databaseName: String,
    val tableList: List<Table>
)

data class Table(
    val databaseName: String,
    val tableName: String,
    val doName: String,
    val comment: String,
    val columns: List<Column>,
    val indexes: List<Index>?,
    val extensions: List<Extension>?
)

data class TableConfig(
    val name: String?,
    val generateTypes: String?
) {
    constructor() : this(null, null)
}

fun TableConfig.generateTypeSet() : Set<String>{
    return generateTypes?.split(",")?.toSet() ?: emptySet()
}

fun Table.getPrimaryColumn(): Column {
    // 优先返回明确通过<<ID>>指定的主键
    getPrimaryIDColumn()?.let { return it }

    return this.columns.first { it.columnType == ColumnType.Primary }
}

fun Table.getPrimaryIDColumn(): Column? {
    return this.columns.firstOrNull { it.columnType == ColumnType.PrimaryID }
}

fun Table.getBkColumns(): List<Column>? {
    val bkIndex = indexes?.firstOrNull { it.indexType == IndexType.UniqueBK } ?: return null
    return bkIndex.columns
}

fun Table.getQueryColumns(): List<Column>? {
    if (this.indexes == null) {
        return null
    }

    // 组合唯一索引中的字段，去掉最后一个，可以作为查询条件
    val mayQueryWhenUk: (Index) -> Boolean = {
        (it.indexType == IndexType.UniqueBK || it.indexType == IndexType.Unique) && it.columns.size > 1
    }
    val columnsOfUk = this.indexes.filter { mayQueryWhenUk(it) }
        .flatMap {
            val subColumns = it.columns.take(it.columns.size - 1)
            subColumns
        }

    // 普通索引里的所有字段，可以作为查询条件
    val skipUpdateColumnNames = SkipUpdateColumn.values().map { it.name.lowercase() }.toSet()
    val columnsOfIndex = this.indexes
        .filter { it.indexType == IndexType.Index }
        .flatMap { it.columns }
        .filter { !skipUpdateColumnNames.contains(it.columnName) }

    val columnsOfLikeQuery = this.columns.filter { it.likeQuery }

    val columns = mutableListOf<Column>()
    columns.addAll(columnsOfUk)
    columns.addAll(columnsOfIndex)
    columns.addAll(columnsOfLikeQuery)
    return columns.distinct()
}


private fun Table.hasQueryField(): Boolean {
    return this.getQueryColumns()?.isNotEmpty() ?: false
}

fun Table.needGenPageNumQuery(): Boolean {
    return this.hasQueryField()
}

fun Table.needGenOffsetQuery(): Boolean {
    if (!this.hasQueryField()) {
        return false
    }

    val primaryColumn = this.getPrimaryColumn()
    return primaryColumn.columnName == "id" && primaryColumn.autoIncrement
}

data class Column(
    val columnName: String,
    val type: String,
    val length: Int?,
    val autoIncrement: Boolean,
    val notNull: Boolean,
    val defaultValue: String?,
    val comment: String?,
    var columnType: ColumnType,
    val autoTimestamp: Boolean,
    val likeQuery: Boolean
)

data class Index(val indexName: String, val indexType: IndexType, val columns: List<Column>)

data class Extension(val columnName: String, val fieldName: String, val fieldType: String, val comment: String?)

data class ExtensionField(val fieldName: String, val fieldType: String, val comment: String?)

// 以下是渲染时用到的类型

data class Field(
    val fieldName: String,
    val fileType: String,
    val comment: String?,
    val notNull: Boolean
)

data class XmlColumn(
    val columnName: String,
    val columnValueName: String,
    val columnType: ColumnType,
    var notNullQueryField: Boolean,
    var autoIncrement: Boolean,
    // is the last column in the index key
    var likeQuery: Boolean
)

data class DdlColumn(
    val columnName: String,
    val type: String,
    val notNull: String?,
    val defaultValue: String?,
    val isPrimary: Boolean,
    val autoIncrement: String?,
    val autoTimestamp: Boolean
)

data class DdlIndex(val type: String, val name: String?, val columnNames: String)


/**
 * used for rendering with velocity model
 */
abstract class RenderModel(open val tableInfo: TableInfo)


// 以下是渲染model的子类

data class GetByUkSection(val bigUkName: String, val whereColumns: List<XmlColumn>)

data class MapperXml(
    override val tableInfo: TableInfo,
    val primaryColumn: XmlColumn,
    val bkColumns: List<XmlColumn>,
    val insertColumns: List<XmlColumn>,
    val updateColumns: List<XmlColumn>,
    val queryWhereColumns: List<XmlColumn>,
    val supportGetByBk: Boolean,
    val supportBatchGet: Boolean,
    val needGenPageNumQuery: Boolean,
    val needGenOffsetQuery: Boolean,
    val getByUkSections: List<GetByUkSection>
) : RenderModel(tableInfo)

data class DdlTable(
    override val tableInfo: TableInfo,
    val columns: List<DdlColumn>,
    val indexes: List<DdlIndex>?
) : RenderModel(tableInfo)

data class TableInfo(
    val packageName: String,
    val databaseName: String,
    val table: Table,
    val render: BaseRender
) {
    // don't delete the unused properties. it's maybe used at velocity templates
    val tableName: String
        get() {
            return table.tableName
        }

    val doName: String
        get() {
            return table.doName
        }

    val doClassName: String
        get() {
            return render.getClassName(table.doName, true, SupportType.DO.name)
        }

    val doInstanceName: String
        get() {
            return render.getClassName(table.doName, false, SupportType.DO.name)
        }

    val entityClassName: String
        get() {
            return render.getClassName(table.doName, true, SupportType.Entity.name)
        }

    val entityInstanceName: String
        get() {
            return render.getClassName(table.doName, false, SupportType.Entity.name)
        }

    val daoClassName: String
        get() {
            return render.getClassName(table.doName, true, SupportType.DAO.name)
        }

    val daoInstanceName: String
        get() {
            return render.getClassName(table.doName, false, SupportType.DAO.name)
        }

    val daoTestClassName: String
        get() {
            return render.getClassName(table.doName, true, SupportType.DaoTest.name)
        }

    val pageNumQueryClassName: String
        get() {
            return render.getClassName(table.doName, true, SupportType.PageNumQuery.name)
        }

    val offsetQueryClassName: String
        get() {
            return render.getClassName(table.doName, true, SupportType.OffsetQuery.name)
        }

    val converterClassName: String
        get() {
            return render.getClassName(table.doName, true, SupportType.Converter.name)
        }

    val converterInstanceName: String
        get() {
            return render.getClassName(table.doName, false, SupportType.Converter.name)
        }


    val repositoryClassName: String
        get() {
            return render.getClassName(table.doName, true, SupportType.Repository.name)
        }

    val bKClassName: String
        get() {
            return render.getClassName(table.doName, true, SupportType.BK.name)
        }
}