package com.github.camork.filesystem.zstd

import com.github.camork.filesystem.ArchiveHandlerBase
import com.intellij.util.io.FileAccessorCache
import java.io.IOException

/**
 * @author Charles Wu
 */
class ZstdFileHandler(path: String) : ArchiveHandlerBase<ZstdFile>(path) {

    override fun getFileAccessor(): FileAccessorCache<ArchiveHandlerBase<ZstdFile>, ZstdFile> {
        return fileAccessor
    }

    companion object {
        private val fileAccessor = object : FileAccessorCache<ArchiveHandlerBase<ZstdFile>, ZstdFile>(20, 10) {

            @Throws(IOException::class)
            override fun createAccessor(key: ArchiveHandlerBase<ZstdFile>): ZstdFile {
                setFileAttributes(key, key.getCanonicalPath())
                return ZstdFile(key.file.canonicalFile)
            }

            override fun isEqual(val1: ArchiveHandlerBase<ZstdFile>, val2: ArchiveHandlerBase<ZstdFile>): Boolean {
                return val1 == val2
            }

            @Throws(IOException::class)
            override fun disposeAccessor(fileAccessor: ZstdFile) {
                fileAccessor.close()
            }
        }
    }
}