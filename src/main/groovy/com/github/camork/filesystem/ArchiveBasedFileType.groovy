package com.github.camork.filesystem

import com.github.camork.util.CoreUtil
import com.intellij.icons.AllIcons
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.NotNull

import javax.swing.Icon

/**
 * @author Charles Wu
 */
abstract class ArchiveBasedFileType implements FileType {

    @Override
    String getDescription() {
        null
    }

    @Override
    Icon getIcon() {
        AllIcons.FileTypes.Archive
    }

    @Override
    String getCharset(@NotNull VirtualFile file, @NotNull byte[] content) {
        null
    }

    @Override
    boolean isBinary() {
        true
    }

    @Override
    boolean isReadOnly() {
        false
    }

}