package com.github.camork.filesystem.gz

import com.github.camork.filesystem.ArchiveHandlerBase
import com.intellij.util.io.FileAccessorCache
import java.io.IOException

/**
 * @author Charles Wu
 */
class GZFileHandler(path: String) : ArchiveHandlerBase<GZFile>(path) {

    override fun getFileAccessor(): FileAccessorCache<ArchiveHandlerBase<GZFile>, GZFile> {
        return fileAccessor
    }

    companion object {
        private val fileAccessor = object : FileAccessorCache<ArchiveHandlerBase<GZFile>, GZFile>(20, 10) {

            @Throws(IOException::class)
            override fun createAccessor(key: ArchiveHandlerBase<GZFile>): GZFile {
                setFileAttributes(key, key.getCanonicalPath())
                return GZFile(key.file.canonicalFile)
            }

            override fun isEqual(val1: ArchiveHandlerBase<GZFile>, val2: ArchiveHandlerBase<GZFile>): Boolean {
                return val1 == val2
            }

            @Throws(IOException::class)
            override fun disposeAccessor(fileAccessor: GZFile) {
                fileAccessor.close()
            }
        }
    }
}