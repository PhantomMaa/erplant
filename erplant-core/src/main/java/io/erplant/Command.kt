package io.erplant

import com.fasterxml.jackson.databind.ObjectMapper
import io.erplant.converter.*
import java.nio.file.Files
import java.nio.file.Paths

class Command {

    companion object {

        private var databasesMap: MutableMap<String, List<Database>> = mutableMapOf()

        private fun reuseDatabases(src: String): List<Database> {
            require(src.endsWith(".puml")) { "file format error, only support .puml" }

            val path = Paths.get(src)
            require(path.toFile().exists()) { "the file: $src not exist" }

            // 读取puml文件，并解析成java对象
            return databasesMap.computeIfAbsent(src) {
                val content = String(Files.readAllBytes(path))
                LineParser().parse(content)
            }
        }

        /**
         * run erplant generate process
         *
         * @param src the .puml source file path
         * @param packageName the package name of generated java source files
         * @param generateTypes ALL or some SupportType
         * @param output the output directory path
         * @param jdkVersion example: 8, 9, 11, 17 ...
         */
        fun run(src: String, packageName: String, generateTypes: String, tableConfigs: String, output: String, jdkVersion: Int): Int {
            val generateTypeSet: Set<String> = if (generateTypes == StrConstant.STR_ALL) {
                SupportType.values().map { it.name }.toSet()
            } else {
                generateTypes.split(",").toSet()
            }
            if (generateTypeSet.isEmpty()) {
                return 0
            }

            val tableConfigArray = ObjectMapper().readValue(tableConfigs, Array<TableConfig>::class.java)
            val tableConfigMap: Map<String, TableConfig> = tableConfigArray?.associateBy { it.name!! } ?: emptyMap()

            // 用于生成代码的render
            val baseRender = BaseRender()
            val velocityRender = VelocityRender()

            val converters = listOf(
                BkConverter(packageName, baseRender, tableConfigMap),
                PageNumQueryConverter(packageName, baseRender, tableConfigMap),
                OffsetQueryConverter(packageName, baseRender, tableConfigMap),
                DoConverter(packageName, baseRender, tableConfigMap),
                DaoConverter(packageName, baseRender, tableConfigMap),
                DaoTestConverter(packageName, baseRender, tableConfigMap, jdkVersion),
                RepositoryConverter(packageName, baseRender, tableConfigMap, jdkVersion),
                MapStructConverter(packageName, baseRender, tableConfigMap),
                EntityConverter(packageName, baseRender, tableConfigMap),
                MapperConverter(packageName, velocityRender, tableConfigMap)
            )

            val usedConverters = converters.filter { generateTypeSet.contains(it.getSupportType().name) }

            val databases = reuseDatabases(src)
            // 将中间对象，进行转换，分别生成DO类，和建表语句
            databases.forEach { database ->

                // 每个database对象，应用一遍各个converter的转换逻辑，为每张表生成转换结果，并输出（DO、DAO、ddl等）
                usedConverters.forEach { converter ->
                    val tableList = converter.convert(database)
                    tableList.forEach {
                        val fullDirectory = "$output/" + converter.getOutputDirectory(database.databaseName)
                        OutputFactory.getOutputer().write(fullDirectory, it.fileName, it.content)
                    }
                }

                // 为每个表生成建表语句，一个db下所有的建表语句放在一个文件
                val ddlConverter = DdlConverter(packageName, velocityRender)
                if (generateTypeSet.contains(ddlConverter.getSupportType().name)) {
                    val ddlList = ddlConverter.convert(database)
                    val fullDirectory = "$output/" + ddlConverter.getOutputDirectory(database.databaseName)
                    val dbResult = ddlList.map { it.content }.joinToString(separator = "\n") { it }
                    val fileName = "V1.0__${database.databaseName}_base.sql"
                    OutputFactory.getOutputer().write(fullDirectory, fileName, dbResult)
                }
            }

            return 1
        }
    }

}