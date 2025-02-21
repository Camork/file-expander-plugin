package com.github.camork.filesystem.sevenzip

import com.github.camork.filesystem.ArchiveHandlerBase
import com.intellij.util.io.FileAccessorCache
import java.io.IOException

/**
 * @author Charles Wu
 */
class SevenZipFileHandler(path: String) : ArchiveHandlerBase<SevenZipFile>(path) {

    override fun getFileAccessor(): FileAccessorCache<ArchiveHandlerBase<SevenZipFile>, SevenZipFile> {
        return fileAccessor
    }

    companion object {
        private val fileAccessor = object : FileAccessorCache<ArchiveHandlerBase<SevenZipFile>, SevenZipFile>(20, 10) {

            @Throws(IOException::class)
            override fun createAccessor(key: ArchiveHandlerBase<SevenZipFile>): SevenZipFile {
                setFileAttributes(key, key.getCanonicalPath())
                return SevenZipFile(key.file.canonicalFile)
            }

            override fun isEqual(val1: ArchiveHandlerBase<SevenZipFile>, val2: ArchiveHandlerBase<SevenZipFile>): Boolean {
                return val1 == val2
            }

            @Throws(IOException::class)
            override fun disposeAccessor(fileAccessor: SevenZipFile) {
                fileAccessor.close()
            }
        }
    }
}