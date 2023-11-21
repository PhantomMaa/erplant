package io.erplant

import io.erplant.utils.HumpUtil
import org.apache.velocity.Template
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
import java.io.StringWriter

open class BaseRender {

    /**
     * get the ClassName, or the instanceName. split by the bigHump
     */
    fun getClassName(tableName: String, bigHump: Boolean, type: String): String {
        val converterType = SupportType.valueOf(type)
        return HumpUtil.lineToHump(tableName, bigHump) + converterType.suffix
    }

    /**
     * get the complete package name
     */
    fun getFullPackageName(packageName: String, databaseName: String, type: String): String {
        val supportType = SupportType.valueOf(type)
        return "$packageName.$databaseName.${supportType.directory}"
    }
}

class VelocityRender : BaseRender() {

    fun output(templateName: String, model: RenderModel): String {
        val engine = VelocityEngine()
        engine.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath")
        engine.setProperty("resource.loader.classpath.class", ClasspathResourceLoader::class.java.name)
        engine.setProperty(RuntimeConstants.INPUT_ENCODING, "utf-8")
        engine.init()

        val context = VelocityContext()
        val map = objToMap(model)
        map.forEach { (key, value) -> context.put(key, value) }
        context.put("tool", RenderTool(this))

        val out = StringWriter()
        val filePath = "velocity/$templateName.vm"
        val template: Template = engine.getTemplate(filePath)
        template.merge(context, out)
        return out.toString()
    }

    /**
     * POJO Class convert to Map
     */
    private fun objToMap(obj: Any): Map<String, Any> {
        return obj.javaClass.declaredFields.associate {
            it.isAccessible = true
            it.name to it.get(obj)
        }
    }
}

/**
 * 渲染输出结果时使用。定义一些方法，方便获取输出时所需的一些值
 */
class RenderTool(val render: BaseRender) {

    /**
     * get the complete class name.
     * don't delete the method, it's used at velocity templates
     */
    fun getFullClassName(packageName: String, databaseName: String, tableName: String, type: String): String {
        return render.getFullPackageName(packageName, databaseName, type) +
                "." + render.getClassName(tableName, true, type)
    }

    fun lineToHump(columnName: String): String {
        return HumpUtil.lineToHump(columnName, false)
    }

}
