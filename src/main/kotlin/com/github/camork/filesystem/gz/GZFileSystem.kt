package com.github.camork.filesystem.gz

import com.github.camork.filesystem.ArchiveBasedFileSystem
import com.github.camork.filesystem.ArchiveBasedFileType
import com.github.camork.filesystem.tar.TarGzFileType
import com.github.camork.util.CoreUtil
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.impl.ArchiveHandler
import com.intellij.openapi.vfs.newvfs.VfsImplUtil

/**
 * @author Charles Wu
 */
object GZFileType : ArchiveBasedFileType() {

    override fun getName(): String = "GZIP"

    override fun getDefaultExtension(): String = "gz"
}

abstract class GZFileSystem : ArchiveBasedFileSystem() {

    companion object {
        const val PROTOCOL = "gzip"

        init {
            CoreUtil.ARCHIVE_EXTENSIONS.add(".$PROTOCOL")
        }

        @JvmStatic
        fun getInstance(): GZFileSystem {
            return VirtualFileManager.getInstance().getFileSystem(PROTOCOL) as GZFileSystem
        }
    }

    override fun getHandler(entryFile: VirtualFile): ArchiveHandler {
        return VfsImplUtil.getHandler(this, entryFile) { path -> GZFileHandler(path) }
    }

    override fun isCorrectFileType(local: VirtualFile): Boolean {
        val fileType: FileType = FileTypeRegistry.getInstance().getFileTypeByFileName(local.name)
        return fileType == GZFileType || fileType == TarGzFileType
    }
}

class GZFileSystemImpl : GZFileSystem() {

    override fun getProtocol(): String = PROTOCOL
}