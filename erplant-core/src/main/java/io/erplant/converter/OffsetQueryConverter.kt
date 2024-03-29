package io.erplant.converter

import com.squareup.javapoet.*
import io.erplant.*
import javax.lang.model.element.Modifier

class OffsetQueryConverter(packageName: String, val render: BaseRender, tableConfigMap: Map<String, TableConfig>) :
    JavaPoetConverter(packageName, tableConfigMap) {

    override fun getSupportType(): SupportType {
        return SupportType.OffsetQuery
    }

    override fun convertTable(databaseName: String, table: Table): FileContent? {
        if (!table.needGenPageNumQuery()) {
            return null
        }

        val fileName = super.getOutFileName(table.doName)
        val tableInfo = TableInfo(packageName, databaseName, table, render)

        val fields = table.getQueryColumns()!!.map { columnToFieldSpec(it) }
        val constructMethod = genConstructMethod(fields)

        val javaDocAuthor = "auto generated by erplant."
        val equalsAndHashCodeAnnotationSpec = AnnotationSpec.builder(ClassName.get("lombok", "EqualsAndHashCode"))
            .addMember("callSuper", "false")
            .build()
        val typeSpec = TypeSpec.classBuilder(tableInfo.offsetQueryClassName)
            .addJavadoc(javaDocAuthor)
            .addAnnotation(ClassName.get("lombok", "Data"))
            .addAnnotation(ClassName.get("lombok", "NoArgsConstructor"))
            .addAnnotation(equalsAndHashCodeAnnotationSpec)
            .superclass(ClassName.get("com.hellocorp.automq.ddd", "OffsetQuery"))
            .addModifiers(Modifier.PUBLIC)
            .addFields(fields)
            .addMethod(constructMethod)
            .build()
        val javaFile = JavaFile.builder(tableInfo.getOffsetQueryClass().packageName(), typeSpec)
            .indent(StrConstant.INDENT)
            .build()
        return FileContent(fileName, javaFile.toString())
    }

    private fun genConstructMethod(fieldSpecs: List<FieldSpec>): MethodSpec {
        val methodBuilder = MethodSpec.constructorBuilder()
        methodBuilder.addModifiers(Modifier.PUBLIC)
            .addAnnotation(ClassName.get("lombok", "Builder"))
            .addParameter(ClassName.get("", "Integer"), "pageSize")
            .addParameter(ClassName.get("", "Long"), "offset")
            .addParameter(ClassName.get("com.hellocorp.automq.ddd", "OrderType"), "orderType")

        fieldSpecs.forEach { methodBuilder.addParameter(ParameterSpec.builder(it.type, it.name).build()) }
        methodBuilder.addStatement("super(pageSize, offset, orderType)")
        fieldSpecs.forEach { methodBuilder.addStatement("this.${it.name} = ${it.name}") }
        return methodBuilder.build()
    }

}
