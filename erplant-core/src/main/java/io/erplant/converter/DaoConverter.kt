package io.erplant.converter

import com.squareup.javapoet.*
import io.erplant.*
import io.erplant.utils.HumpUtil
import javax.lang.model.element.Modifier

class DaoConverter(packageName: String, val render: BaseRender, tableConfigMap: Map<String, TableConfig>) :
    JavaPoetConverter(packageName, tableConfigMap) {

    override fun getSupportType(): SupportType {
        return SupportType.DAO
    }

    override fun convertTable(databaseName: String, table: Table): FileContent {
        val fileName = super.getOutFileName(table.doName)
        val tableInfo = TableInfo(packageName, databaseName, table, render)
        val methods = genMethods(table, tableInfo)

        val fullPackageName = tableInfo.getDaoClass().packageName()
        val className = tableInfo.daoClassName
        val javaDocAuthor = "auto generated by erplant."
        val typeSpec = TypeSpec.interfaceBuilder(className)
            .addJavadoc(javaDocAuthor)
            .addAnnotation(ClassName.get("org.apache.ibatis.annotations", "Mapper"))
            .addModifiers(Modifier.PUBLIC)
            .addMethods(methods)
            .build()
        val javaFile = JavaFile.builder(fullPackageName, typeSpec).indent(StrConstant.INDENT).build()
        return FileContent(fileName, javaFile.toString())
    }

    private fun genMethods(table: Table, tableInfo: TableInfo): List<MethodSpec> {
        val methods = mutableListOf(
            genInsert(tableInfo),
            genGet(tableInfo),
            genGetByBk(table, tableInfo),
            genGetAndLockByBk(table, tableInfo),
            genPageNumQuery(table, tableInfo),
            genOffsetQuery(table, tableInfo),
            genCount(table),
            genUpdate(tableInfo),
            genDelete(tableInfo)
        )

        val supportBatchGet = super.isSupportBatchGet(table)
        if (supportBatchGet) {
            methods.add(genBatchGet(tableInfo))
        }

        val methodGetByUks =
            table.indexes?.filter { it.indexType == IndexType.Unique }?.map { genGetByUk(tableInfo, it) } ?: emptyList()
        methods.addAll(methodGetByUks)
        return methods.filterNotNull()
    }

    private fun genInsert(tableInfo: TableInfo): MethodSpec {
        val param = ParameterSpec.builder(tableInfo.getDoClass(), tableInfo.doInstanceName).build()
        return MethodSpec.methodBuilder("insert")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addParameter(param)
            .returns(TypeName.INT)
            .build()
    }

    private fun genGet(tableInfo: TableInfo): MethodSpec {
        val pkField = tableInfo.getPrimaryField()
        val pkClassName = ClassName.get("", pkField.fileType)
        val param = ParameterSpec.builder(pkClassName, pkField.fieldName).build()
        return MethodSpec.methodBuilder("get")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addParameter(param)
            .returns(tableInfo.getDoClass())
            .build()
    }

    private fun genPageNumQuery(table: Table, tableInfo: TableInfo): MethodSpec? {
        if (!table.needGenPageNumQuery()) {
            return null
        }

        val listClass = ClassName.get("java.util", "List")
        val typedListClass: TypeName = ParameterizedTypeName.get(listClass, tableInfo.getDoClass())
        val param = ParameterSpec.builder(tableInfo.getPageNumQueryClass(), "query").build()
        return MethodSpec.methodBuilder("pageNumQuery")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addParameter(param)
            .returns(typedListClass)
            .build()
    }

    private fun genOffsetQuery(table: Table, tableInfo: TableInfo): MethodSpec? {
        if (!table.needGenOffsetQuery()) {
            return null
        }

        val listClass = ClassName.get("java.util", "List")
        val typedListClass: TypeName = ParameterizedTypeName.get(listClass, tableInfo.getDoClass())
        val param = ParameterSpec.builder(tableInfo.getOffsetQueryClass(), "query").build()
        return MethodSpec.methodBuilder("offsetQuery")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addParameter(param)
            .returns(typedListClass)
            .build()
    }

    private fun genCount(table: Table): MethodSpec? {
        if (!table.needGenPageNumQuery()) {
            return null
        }

        val param = ParameterSpec.builder(ClassName.get("com.hellocorp.automq.ddd", "Query"), "query").build()
        return MethodSpec.methodBuilder("count")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addParameter(param)
            .returns(TypeName.INT)
            .build()
    }

    private fun genUpdate(tableInfo: TableInfo): MethodSpec {
        val param = ParameterSpec.builder(tableInfo.getDoClass(), tableInfo.doInstanceName).build()
        return MethodSpec.methodBuilder("update")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addParameter(param)
            .returns(TypeName.INT)
            .build()
    }

    private fun genDelete(tableInfo: TableInfo): MethodSpec {
        val pkField = tableInfo.getPrimaryField()
        val pkClassName = ClassName.get("", pkField.fileType)
        val param = ParameterSpec.builder(pkClassName, pkField.fieldName).build()
        return MethodSpec.methodBuilder("delete")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addParameter(param)
            .returns(TypeName.INT)
            .build()
    }

    private fun genBatchGet(tableInfo: TableInfo): MethodSpec {
        val listClass = ClassName.get("java.util", "List")
        val pkField = tableInfo.getPrimaryField()
        val pkClassName = ClassName.get("", pkField.fileType)
        val paramListClass: TypeName = ParameterizedTypeName.get(listClass, pkClassName)
        val resultListClass: TypeName = ParameterizedTypeName.get(listClass, tableInfo.getDoClass())
        val paramAnnotation = ClassName.get("org.apache.ibatis.annotations", "Param")
        val paramAnnotationSpec = AnnotationSpec.builder(paramAnnotation).addMember("value", "\$S", "ids").build()
        val param = ParameterSpec.builder(paramListClass, "ids").addAnnotation(paramAnnotationSpec).build()
        return MethodSpec.methodBuilder("batchGet")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addParameter(param)
            .returns(resultListClass)
            .build()
    }

    private fun genGetByBk(table: Table, tableInfo: TableInfo): MethodSpec? {
        table.indexes?.firstOrNull { it.indexType == IndexType.UniqueBK } ?: return null

        val param = ParameterSpec.builder(tableInfo.getBkClass(), "bk").build()
        return MethodSpec.methodBuilder("getByBk")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addParameter(param)
            .returns(tableInfo.getDoClass())
            .build()
    }

    private fun genGetAndLockByBk(table: Table, tableInfo: TableInfo): MethodSpec? {
        table.indexes?.firstOrNull { it.indexType == IndexType.UniqueBK } ?: return null

        val param = ParameterSpec.builder(tableInfo.getBkClass(), "bk").build()
        return MethodSpec.methodBuilder("getAndLockByBk")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addParameter(param)
            .returns(tableInfo.getDoClass())
            .build()
    }

    private fun genGetByUk(tableInfo: TableInfo, ukIndex: Index): MethodSpec {
        val fields = ukIndex.columns.map {
            columnToField(it)
        }
        val params = fields.map {
            val fieldClass = super.fieldTypeToJavaClass(it.fileType)
            ParameterSpec.builder(fieldClass, it.fieldName).build()
        }

        val bigIndexName = HumpUtil.lineToHump(ukIndex.indexName.split("uk_")[1], true)
        return MethodSpec.methodBuilder("getBy${bigIndexName}")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addParameters(params)
            .returns(tableInfo.getDoClass())
            .build()
    }
}