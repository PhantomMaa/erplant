package io.erplant.output

import io.erplant.Output
import java.io.BufferedWriter
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths

open class CompleteOutput : Output {

    override fun write(fullDirectory: String, fileName: String, content: String) {
        doWrite(fullDirectory, fileName, content)
        printLog(fileName)
    }

    private fun printLog(fileName: String) {
        println("overwrite generated $fileName")
    }

    /**
     * only do write operation
     */
    private fun doWrite(fullDirectory: String, fileName: String, content: String) {
        val fullDirectoryPath = Paths.get(fullDirectory)
        val fullFileName = "$fullDirectory/$fileName"
        Files.createDirectories(fullDirectoryPath)
        val bufferedWriter = BufferedWriter(FileWriter(fullFileName))
        bufferedWriter.write(content)
        bufferedWriter.flush()
        bufferedWriter.close()
    }

}
