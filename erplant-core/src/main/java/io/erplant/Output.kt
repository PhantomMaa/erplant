package io.erplant

import io.erplant.output.CompleteOutput


interface Output {
    fun write(fullDirectory: String, fileName: String, content: String)
}

object OutputFactory {

    @JvmStatic
    fun getOutputer(): Output {
        return CompleteOutput()
    }
}