package io.erplant.utils

object JavaCodeUtil {

    fun resolve(content: String): JavaSourceCode {
        val allLines = content.split("\n").filter { it.isNotBlank() }
        val packageName = allLines.firstOrNull { it.startsWith("package ") }
        val imports = allLines.filter { it.startsWith("import ") }
        val importEndIndex = allLines.indexOfLast { it.startsWith("import ") }
        val annotationStartIndex = allLines.indexOfFirst { it.startsWith("@") }
        val annotationEndIndex = allLines.indexOfLast { it.startsWith("@") }
        val comment = allLines.subList(importEndIndex + 1, annotationStartIndex).joinToString(separator = "\n") { it }
        val annotations = allLines.filter { it.startsWith("@") }
        val classDeclareLine = allLines.subList(annotationEndIndex + 1, annotationEndIndex + 2).joinToString(separator = "\n") { it }

        var fields: List<String>? = null
        var methods: List<MethodSection>? = null
        var construct: String? = null
        val classStartIndex: Int
        val words = classDeclareLine.split(" ")
        when {
            classDeclareLine.contains(" class ") -> {
                classStartIndex = words.indexOfFirst { it == "class" }

                fields = allLines.filter { it.contains("private ") && it.trimEnd().endsWith(";") }
                val constructStartIndex = allLines.indexOfLast { it.trimStart().startsWith("@Builder") }
                val dropedLines = allLines.drop(constructStartIndex)
                val constructEndIndex = dropedLines.indexOfFirst { it.trimStart().startsWith("}") }
                construct = dropedLines.subList(0, constructEndIndex + 1).joinToString(separator = "\n") { it }
            }
            classDeclareLine.contains(" interface ") -> {
                classStartIndex = words.indexOfFirst { it == "interface" }

                val classLineStartIndex = allLines.indexOfLast { it.contains("public interface") }
                val dropedLines = allLines.take(allLines.size - 1).drop(classLineStartIndex + 1)
                methods = mutableListOf()
                forwardSplitMethod(dropedLines, methods)
            }
            else -> {
                throw RuntimeException("can not parseClassName")
            }
        }
        val className = words.subList(classStartIndex + 1, classStartIndex + 2).joinToString(separator = "\n") { it }
        return JavaSourceCode(packageName, className, imports, comment, annotations, classDeclareLine, fields, construct, methods)
    }

    private fun forwardSplitMethod(lines: List<String>, methods: MutableList<MethodSection>) {
        if (lines.isEmpty()) {
            return
        }

        var startIndex = lines.indexOfFirst { it.endsWith("*/") }
        val endIndex = lines.indexOfFirst { it.endsWith(");") }
        startIndex = when {
            startIndex > endIndex -> {
                0
            }
            startIndex > 0 -> {
                startIndex + 1
            }
            else -> {
                0
            }
        }
        val methodLines = lines.subList(startIndex, endIndex + 1)
        val commentAndMethodLines = lines.subList(0, endIndex + 1)
        val joinedMethodLines = commentAndMethodLines.joinToString(separator = "\n") { it }

        val methodKey = methodLines.filter { !it.contains("@Deprecated") }.joinToString(separator = " ") { it.trim() }.trim()
        val methodSection = MethodSection(methodKey, joinedMethodLines)
        methods.add(methodSection)
        val dropLines = lines.drop(endIndex + 1)
        forwardSplitMethod(dropLines, methods)
    }
}

data class JavaSourceCode(
        val packageName: String?, val className: String, var imports: List<String>, val comment: String,
        val annotations: List<String>, val classDeclareLine: String, var fields: List<String>?,
        var construct: String?, var methods: List<MethodSection>?) {

    override fun toString(): String {
        val methodSection = this.methods?.map { it.methodLines }?.joinToString(separator = "\n\n") { it }
        val sb = StringBuffer()
        sb.append(this.packageName).append("\n\n")
                .append(this.imports.joinToString(separator = "\n") { it }).append("\n\n")
                .append(this.comment).append("\n")
                .append(this.annotations.joinToString(separator = "\n") { it }).append("\n")
                .append(this.classDeclareLine).append("\n\n")

        if (this.fields?.isNotEmpty()!!) {
            sb.append(this.fields!!.joinToString(separator = "\n\n") { it }).append("\n\n")
        }

        if (this.construct != null) {
            sb.append(this.construct).append("\n\n")
        }

        if (!methodSection.isNullOrBlank()) {
            sb.append(methodSection).append("\n\n")
        }

        sb.append("}")
        return sb.toString()
    }
}

data class MethodSection(val methodKey: String, var methodLines: String)