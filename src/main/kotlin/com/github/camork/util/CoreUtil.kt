package com.github.camork.util

import com.intellij.openapi.vfs.JarFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.util.*

object CoreUtil {

    const val DOT = '.'
    const val ZIP_EXTENSION = "zip"
    const val WAR_EXTENSION = "war"
    const val NESTED_FILE_TEMP_FOLDER = "fileExpanderFiles"

     val ARCHIVE_EXTENSIONS: MutableSet<String> = linkedSetOf(
        "$DOT$ZIP_EXTENSION",
        "$DOT$WAR_EXTENSION",
        "$DOT${JarFileSystem.PROTOCOL}",
    )

    /**
     * Temporary folder to store nested archive files
     */
    @Throws(IOException::class)
    @JvmStatic
    fun getTempDirectory(): Path {
        val baseDir = File(System.getProperty("java.io.tmpdir"))
        val tempDir = File(baseDir, NESTED_FILE_TEMP_FOLDER)
        if ((tempDir.exists() && tempDir.canWrite()) || tempDir.mkdir()) {
            return tempDir.toPath()
        }
        throw IOException("Folder: ${tempDir.absolutePath} cannot be written!")
    }

    /**
     * return a temporary file path name in which is based on entry path and timestamp
     */
    @Throws(IOException::class)
    @JvmStatic
    fun getTempFilePath(nestedFile: VirtualFile): Path {
        val baseDir = getTempDirectory()
        val hash = Objects.hash(nestedFile.path, nestedFile.timeStamp)
        return baseDir.resolve("${nestedFile.name}-$hash").resolve(nestedFile.name)
    }
}