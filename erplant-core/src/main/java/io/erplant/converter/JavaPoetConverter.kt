package io.erplant.converter

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import io.erplant.*
import io.erplant.utils.HumpUtil
import javax.lang.model.element.Modifier

/**
 * 使用javapoet库解析java语法的Converter
 */
abstract class JavaPoetConverter(packageName: String, tableConfigMap: Map<String, TableConfig>) : JavaConverter(packageName, tableConfigMap) {

    fun TableInfo.getPrimaryField():Field {
        val pkColumn = table.getPrimaryColumn()
        return Field(
            fieldName = HumpUtil.lineToHump(pkColumn.columnName, false),
            fileType = convertToFieldType(pkColumn),
            comment = pkColumn.comment,
            notNull = pkColumn.notNull
        )
    }

    // 以下，扩展TableInfo的方法，和解析java语法相关的
    fun TableInfo.getDoClass(): ClassName {
        val doClassPackage = render.getFullPackageName(packageName, databaseName, SupportType.DO.name)
        return ClassName.get(doClassPackage, doClassName)
    }

    fun TableInfo.getDaoClass(): ClassName {
        val daoClassPackage = render.getFullPackageName(packageName, databaseName, SupportType.DAO.name)
        return ClassName.get(daoClassPackage, daoClassName)
    }

    fun TableInfo.getDaoTestClass(): ClassName {
        val daoTestClassPackage = render.getFullPackageName(packageName, databaseName, SupportType.DaoTest.name)
        return ClassName.get(daoTestClassPackage, daoTestClassName)
    }

    fun TableInfo.getPageNumQueryClass(): ClassName {
        val queryClassPackage = render.getFullPackageName(packageName, databaseName, SupportType.PageNumQuery.name)
        return ClassName.get(queryClassPackage, pageNumQueryClassName)
    }

    fun TableInfo.getOffsetQueryClass(): ClassName {
        val queryClassPackage = render.getFullPackageName(packageName, databaseName, SupportType.PageNumQuery.name)
        return ClassName.get(queryClassPackage, offsetQueryClassName)
    }

    fun TableInfo.getBkClass(): ClassName {
        val bKClassPackage = render.getFullPackageName(packageName, databaseName, SupportType.BK.name)
        return ClassName.get(bKClassPackage, bKClassName)
    }

    fun TableInfo.getEntityClass(): ClassName {
        val entityClassPackage = render.getFullPackageName(packageName, databaseName, SupportType.Entity.name)
        return ClassName.get(entityClassPackage, entityClassName)
    }

    fun TableInfo.getConverterClass(): ClassName {
        val converterClassPackage = render.getFullPackageName(packageName, databaseName, SupportType.Converter.name)
        return ClassName.get(converterClassPackage, converterClassName)
    }

    fun TableInfo.getRepositoryClass(): ClassName {
        val repositoryClassPackage = render.getFullPackageName(packageName, databaseName, SupportType.Repository.name)
        return ClassName.get(repositoryClassPackage, repositoryClassName)
    }

    fun fieldTypeToJavaClass(fieldType: String): TypeName {
        return when (fieldType) {
            "Instant", "LocalDate", "LocalDateTime" -> {
                ClassName.get("java.time", fieldType)
            }

            else -> {
                val genericityStartIndex = fieldType.indexOf("<")
                val genericityEndIndex = fieldType.indexOf(">")
                if (genericityStartIndex > 0 && genericityEndIndex > 0) {
                    val typeName = fieldType.substring(0, genericityStartIndex)
                    if (collectionTypes.contains(typeName)) {
                        // common collection classes
                        val collectionType = ClassName.get("java.util", typeName)
                        val genericityName = fieldType.substring(genericityStartIndex + 1, genericityEndIndex)
                        val genericityType = ClassName.get("", genericityName)
                        return ParameterizedTypeName.get(collectionType, genericityType)
                    }
                }
                ClassName.get("", fieldType)
            }
        }
    }

    fun columnToFieldSpec(column: Column): FieldSpec {
        val fieldType = super.convertToFieldType(column)
        val fieldClass = fieldTypeToJavaClass(fieldType)
        val fieldName = HumpUtil.lineToHump(column.columnName, false)
        return FieldSpec.builder(fieldClass, fieldName).addModifiers(Modifier.PRIVATE).build()
    }

    fun genExtensionFields(fields: List<ExtensionField>): List<FieldSpec> {
        return fields.map {
            val fieldClass = fieldTypeToJavaClass(it.fieldType)
            FieldSpec.builder(fieldClass, it.fieldName).addModifiers(Modifier.PRIVATE).build()
        }
    }

}
