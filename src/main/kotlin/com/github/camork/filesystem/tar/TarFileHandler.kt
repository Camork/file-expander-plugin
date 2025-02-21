package com.github.camork.filesystem.tar

import com.github.camork.filesystem.ArchiveHandlerBase
import com.intellij.util.io.FileAccessorCache
import java.io.IOException

/**
 * @author Charles Wu
 */
class TarFileHandler(path: String) : ArchiveHandlerBase<TarFile>(path) {

    override fun getFileAccessor(): FileAccessorCache<ArchiveHandlerBase<TarFile>, TarFile> {
        return fileAccessor
    }

    companion object {
        private val fileAccessor = object : FileAccessorCache<ArchiveHandlerBase<TarFile>, TarFile>(20, 10) {

            @Throws(IOException::class)
            override fun createAccessor(key: ArchiveHandlerBase<TarFile>): TarFile {
                setFileAttributes(key, key.getCanonicalPath())
                return TarFile(key.file.canonicalFile)
            }

            override fun isEqual(val1: ArchiveHandlerBase<TarFile>, val2: ArchiveHandlerBase<TarFile>): Boolean {
                return val1 == val2
            }

            @Throws(IOException::class)
            override fun disposeAccessor(fileAccessor: TarFile) {
                fileAccessor.close()
            }
        }
    }
}