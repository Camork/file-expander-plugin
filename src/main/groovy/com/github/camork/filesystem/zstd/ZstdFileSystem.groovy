package com.github.camork.filesystem.zstd

import com.github.camork.filesystem.ArchiveBasedFileSystem
import com.github.camork.filesystem.gz.GZFileHandler
import com.github.camork.filesystem.tar.TarGzFileType
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.impl.ArchiveHandler
import com.intellij.openapi.vfs.newvfs.VfsImplUtil
import org.jetbrains.annotations.NotNull

class ZstdFileSystem extends ArchiveBasedFileSystem {
    public static final String PROTOCOL = 'zst'

    static ZstdFileSystem getInstance() {
        return VirtualFileManager.getInstance().getFileSystem(PROTOCOL) as ZstdFileSystem
    }

    @Override
    String getProtocol() {
        return PROTOCOL
    }

    @Override
    protected ArchiveHandler getHandler(@NotNull VirtualFile entryFile) {
        return VfsImplUtil.getHandler(this, entryFile, { path -> new ZstdFileHandler(path) })
    }

    @Override
    protected boolean isCorrectFileType(@NotNull VirtualFile local) {
        FileType fileType = FileTypeRegistry.getInstance().getFileTypeByFileName(local.name)
        return fileType == ZstdFileType.INSTANCE
    }
}
