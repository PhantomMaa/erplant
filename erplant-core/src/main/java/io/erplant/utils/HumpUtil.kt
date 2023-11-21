package io.erplant.utils

import java.util.regex.Pattern

object HumpUtil {

    private val LINE_PATTERN = Pattern.compile("_(\\w)")
    private val HUMP_PATTERN = Pattern.compile("[A-Z]")

    /**
     * 下划线转驼峰
     */
    fun lineToHump(str: String, bigHump: Boolean): String {
        if (str.isBlank()) {
            return str
        }

        val matcher = LINE_PATTERN.matcher(str)
        val sb = StringBuffer()
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).uppercase())
        }
        matcher.appendTail(sb)
        val result = sb.toString()
        return if (bigHump) sb[0].uppercase() + result.substring(1) else sb.toString()
    }

    /**
     * 驼峰转下划线
     */
    fun humpToLine(str: String): String {
        if (str.isBlank()) {
            return str
        }

        val matcher = HUMP_PATTERN.matcher(str)
        val sb = StringBuffer()
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).lowercase())
        }
        matcher.appendTail(sb)
        return sb.toString()
    }

}