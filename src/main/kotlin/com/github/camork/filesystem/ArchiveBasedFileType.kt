package com.github.camork.filesystem

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

/**
 * @author Charles Wu
 */
abstract class ArchiveBasedFileType : FileType {

    override fun getDescription(): String {
        return "$name archive"
    }

    override fun getIcon(): Icon {
        return AllIcons.FileTypes.Archive
    }

    override fun getCharset(file: VirtualFile, content: ByteArray): String? = null

    override fun isBinary(): Boolean = true

    override fun isReadOnly(): Boolean = false
}