package com.sml.data.tools

import android.os.Environment
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import javax.inject.Inject

class FileTools @Inject constructor() {

    fun createFileWithSizeByFileName(fileName: String, size: Long): File {
        val file = createEmptyFile(fileName)
        createFileWithSize(file, size)
        return file
    }

    fun createEmptyFile(fileName: String): File =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath, fileName)

    @Throws(IOException::class)
    fun createFileWithSize(inputFile: File, size: Long, mode: String = "rw") {
        val randomAccessFile = RandomAccessFile(inputFile, mode)
        randomAccessFile.setLength(size)
        randomAccessFile.close()
    }
}