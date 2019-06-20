package com.github.camork.filesystem.sevenzip

import com.github.camork.filesystem.ArchiveBasedFileSystem
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.impl.ArchiveHandler
import com.intellij.openapi.vfs.newvfs.VfsImplUtil
import org.jetbrains.annotations.NotNull

/**
 * @author Charles Wu
 */
abstract class SevenZipFileSystem extends ArchiveBasedFileSystem {

    public static final String PROTOCOL = SevenZipFileType.INSTANCE.getDefaultExtension()

    static SevenZipFileSystem getInstance() {
        return VirtualFileManager.getInstance().getFileSystem(PROTOCOL) as SevenZipFileSystem
    }

    @Override
    protected ArchiveHandler getHandler(@NotNull VirtualFile entryFile) {
        return VfsImplUtil.getHandler(this, entryFile, { path -> new SevenZipFileHandler(path) })
    }

    @Override
    protected boolean isCorrectFileType(@NotNull VirtualFile local) {
        return FileTypeRegistry.getInstance().getFileTypeByFileName(local.name) == SevenZipFileType.INSTANCE
    }

}