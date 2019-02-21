package com.github.camork.util

import com.github.camork.filesystem.gz.GZFileSystem
import com.github.camork.filesystem.tar.TarFileSystem
import com.google.common.collect.ImmutableSet
import com.intellij.openapi.vfs.JarFileSystem
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.NotNull

import java.nio.file.Path

/**
 * @author Charles Wu
 */
class CoreUtil {

    public static final String DOT = '.'
    public static final String ZIP_EXTENSION = 'zip'
    public static final String WAR_EXTENSION = 'war'
    public static final String NESTED_FILE_TEMP_FOLDER = 'fileExpanderFiles'

    public static final Set<String> ARCHIVE_EXTENSIONS = ImmutableSet.of(
            DOT + ZIP_EXTENSION,
            DOT + WAR_EXTENSION,
            DOT + JarFileSystem.PROTOCOL,
            DOT + TarFileSystem.PROTOCOL,
            DOT + GZFileSystem.PROTOCOL,
    )

    /**
     * Temporary folder to store nested archive files
     */
    @NotNull
    static Path getTempDirectory() {
        File baseDir = new File(System.getProperty("java.io.tmpdir"))
        File tempDir = new File(baseDir, NESTED_FILE_TEMP_FOLDER)
        if ((tempDir.exists() && tempDir.canWrite()) || tempDir.mkdir()) {
            return tempDir.toPath()
        }

        throw new IOException("Folder: ${tempDir.getAbsolutePath()} cannot be writed!")
    }

    /**
     * return a temporary file path name in which is based on entry path and timestamp
     */
    @NotNull
    static Path getTempFilePath(@NotNull VirtualFile nestedFile) {
        Path baseDir = getTempDirectory()
        int hash = Objects.hash(nestedFile.path, nestedFile.timeStamp)

        return baseDir.resolve("${nestedFile.name}-${hash}").resolve(nestedFile.name)
    }

}
