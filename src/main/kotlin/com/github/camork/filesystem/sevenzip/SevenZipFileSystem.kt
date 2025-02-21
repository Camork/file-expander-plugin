package com.github.camork.filesystem.sevenzip

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
object SevenZipFileType : ArchiveBasedFileType() {

    override fun getName(): String = "7ZIP"

    override fun getDefaultExtension(): String = "7z"
}

abstract class SevenZipFileSystem : ArchiveBasedFileSystem() {

    companion object {
        const val PROTOCOL = "7z"

        fun getInstance(): SevenZipFileSystem {
            return VirtualFileManager.getInstance().getFileSystem(PROTOCOL) as SevenZipFileSystem
        }
    }

    override fun getHandler(entryFile: VirtualFile): ArchiveHandler {
        return VfsImplUtil.getHandler(this, entryFile) { path -> SevenZipFileHandler(path) }
    }

    override fun isCorrectFileType(local: VirtualFile): Boolean {
        return FileTypeRegistry.getInstance().getFileTypeByFileName(local.name) == SevenZipFileType
    }
}

class SevenZipFileSystemImpl : SevenZipFileSystem() {
    override fun getProtocol(): String = PROTOCOL
}