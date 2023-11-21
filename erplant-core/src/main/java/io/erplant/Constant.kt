package io.erplant

class ParseRuleConstant {
    companion object {

        // TODO 对puml文本内容进行语法检查。标记只能出现在正确的区域

        /**
         * 标识数字主键是否是递增。只能出现在主键上
         */
        const val AUTO_INCREMENT = "<<AUTO_INCREMENT>>"

        /**
         * 是索引的标记语法。标识一个unique key是否是业务主键。只能出现在索引区域
         */
        const val BUSSINESS_KEY = "<<BK>>"

        /**
         * 标识一个主键是否是实体表的ID（实体表，它的主键可能被其他表所引用）。只能出现在字段区域
         */
        const val ENTITY_ID = "<<ID>>"

        /**
         * 标识一个datetime字段是否自动生成一个时间。只能出现在字段区域
         */
        const val AUTO_TIMESTAMP = "<<TIMESTAMP>>"

        /**
         * 标识一个字段，表明此字段支持like查询
         */
        const val LIKE_QUERY = "<<LIKE_QUERY>>"

    }
}

class StrConstant {
    companion object {

        const val INDENT = "    "
        const val STR_VARCHAR = "varchar"
        const val STR_ALL = "ALL"
        const val STR_JUNIT5_PACKAGE = "org.junit.jupiter.api"
        const val STR_JAKARTA_PACKAGE = "jakarta.annotation"
        const val STR_JAVAX_PACKAGE = "javax.annotation"
        const val STR_PRIMARY_KEY = "primary key"
        const val STR_UNIQUE_KEY = "unique key"
        const val STR_INDEX = "index"
        const val NOT_NULL = "not_null"

        fun getJakartaPackage(jdkVersion : Int): String {
            if (jdkVersion > 9) {
                return STR_JAKARTA_PACKAGE
            } else {
                return STR_JAVAX_PACKAGE
            }
        }
    }

}


/**
 * update时跳过的字段（其实是用到的，勿删）
 */
enum class SkipUpdateColumn {
    ID,
    GMT_CREATE,
    GMT_MODIFIED,
    VERSION
}

enum class SupportType(val suffix: String, val directory: String) {
    // 以下类型，完全自动生成
    DO("DO", "infra.persist.dataobject"),
    BK("BK", "infra.persist.bk"),
    PageNumQuery("PageNumQuery", "infra.persist.query"),
    OffsetQuery("OffsetQuery", "infra.persist.query"),
    Mapper("mapper", "mapper"),
    DAO("DAO", "infra.persist.dao"),
    DaoTest("DaoTest", "test.dao"),
    Entity("", "domain.entity"),
    Converter("Converter", "domain.converter"),
    Repository("Repository", "domain.repository"),
    Ddl("", "ddl")
}

enum class ColumnType {
    /**
     * 自增主键
     */
    Primary,

    /**
     * 表示是Primary的一种特殊情况。实体表的主键<<ID>>
     */
    PrimaryID,

    /**
     * uk之一。判断逻辑来自
     * @see StrConstant.STR_UNIQUE_KEY
     */
    UniqueKey,

    /**
     * 普通索引
     */
    Index,

    /**
     * 普通字段
     */
    Normal

}

enum class IndexType {
    /**
     * 普通索引
     */
    Index,

    /**
     * 唯一约束
     */
    Unique,

    /**
     * 唯一约束的一种特殊情况。UK并且是业务的唯一标识。判断逻辑来自
     * @see ParseRuleConstant.BUSSINESS_KEY
     */
    UniqueBK

}