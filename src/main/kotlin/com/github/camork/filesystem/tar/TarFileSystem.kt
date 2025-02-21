package com.github.camork.filesystem.tar

import com.github.camork.filesystem.ArchiveBasedFileSystem
import com.github.camork.filesystem.ArchiveBasedFileType
import com.github.camork.util.CoreUtil
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.impl.ArchiveHandler
import com.intellij.openapi.vfs.newvfs.VfsImplUtil

/**
 * @author Charles Wu
 */
object TarFileType : ArchiveBasedFileType() {

    override fun getName(): String = "TAR"

    override fun getDefaultExtension(): String = "tar"
}

object TarGzFileType : ArchiveBasedFileType() {

    override fun getName(): String = "TGZ"

    override fun getDefaultExtension(): String = "tgz"
}

abstract class TarFileSystem : ArchiveBasedFileSystem() {

    companion object {
        const val PROTOCOL = "tar"

        init {
            CoreUtil.ARCHIVE_EXTENSIONS.add(".$PROTOCOL")
        }

        @JvmStatic
        fun getInstance(): TarFileSystem {
            return VirtualFileManager.getInstance().getFileSystem(PROTOCOL) as TarFileSystem
        }
    }

    override fun getHandler(entryFile: VirtualFile): ArchiveHandler {
        return VfsImplUtil.getHandler(this, entryFile) { path -> TarFileHandler(path) }
    }

    override fun isCorrectFileType(local: VirtualFile): Boolean {
        return FileTypeRegistry.getInstance().getFileTypeByFileName(local.name) == TarFileType
    }
}

class TarFileSystemImpl : TarFileSystem() {

    override fun getProtocol(): String {
        return PROTOCOL
    }
}