package com.github.camork.filesystem.zstd

import com.github.camork.filesystem.ArchiveBasedFileSystem
import com.github.camork.filesystem.ArchiveBasedFileType
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.impl.ArchiveHandler
import com.intellij.openapi.vfs.newvfs.VfsImplUtil

/**
 * @author Charles Wu
 */
object ZstdFileType : ArchiveBasedFileType() {

    override fun getName(): String = "ZSTANDARD"

    override fun getDefaultExtension(): String = "zst"
}

class ZstdFileSystem : ArchiveBasedFileSystem() {

    companion object {

        fun getInstance(): ZstdFileSystem {
            return VirtualFileManager.getInstance().getFileSystem(ZstdFileType.getDefaultExtension()) as ZstdFileSystem
        }
    }

    override fun getProtocol(): String {
        return ZstdFileType.getDefaultExtension()
    }

    override fun getHandler(entryFile: VirtualFile): ArchiveHandler {
        return VfsImplUtil.getHandler(this, entryFile) { path -> ZstdFileHandler(path) }
    }

    override fun isCorrectFileType(local: VirtualFile): Boolean {
        return FileTypeRegistry.getInstance().getFileTypeByFileName(local.name) == ZstdFileType
    }
}