package com.github.camork.filesystem

/**
 * @author Charles Wu
 */
object MyArchiveFileType : com.intellij.ide.highlighter.ArchiveFileType() {

    override fun getName(): String {
        return "EXTEND ARCHIVE"
    }
}
