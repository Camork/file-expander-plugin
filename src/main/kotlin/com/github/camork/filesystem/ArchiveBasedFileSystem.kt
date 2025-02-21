package com.github.camork.filesystem

import com.intellij.ide.highlighter.ArchiveFileType
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.ArchiveFileSystem
import com.intellij.openapi.vfs.newvfs.VfsImplUtil
import org.apache.commons.lang3.StringUtils

/**
 * @author Charles Wu
 */
abstract class ArchiveBasedFileSystem : ArchiveFileSystem() {

    companion object {
        const val SEPARATOR = "!/"

        @JvmStatic
        fun isValid(path: String): Boolean {
            return path.contains(SEPARATOR)
        }

        @JvmStatic
        fun containExtension(path: String): Boolean {
            val typeManager = FileTypeManager.getInstance()
            val matchers = typeManager.getAssociations(ArchiveFileType.INSTANCE) + typeManager.getAssociations(MyArchiveFileType)
            return matchers.any {
                it.acceptsCharSequence(path)
            }
        }

        @JvmStatic
        fun isNestedFile(path: String): Boolean {
            return if (StringUtils.countMatches(path, SEPARATOR) > 0) containExtension(path) else false
        }

    }

    override fun composeRootPath(localPath: String): String {
        return localPath + SEPARATOR
    }

    override fun extractLocalPath(rootPath: String): String {
        return StringUtil.trimEnd(rootPath, SEPARATOR)
    }

    override fun extractRootPath(path: String): String {
        val jarSeparatorIndex = path.indexOf(SEPARATOR)
        require(jarSeparatorIndex >= 0) { "Path passed to ${this::class.java} must have separator '$SEPARATOR': $path" }
        return path.substring(0, jarSeparatorIndex + SEPARATOR.length)
    }

    override fun findFileByPathIfCached(path: String): VirtualFile? {
        return if (isValid(path)) VfsImplUtil.findFileByPathIfCached(this, path) else null
    }

    override fun findFileByPath(path: String): VirtualFile? {
        return if (isValid(path)) VfsImplUtil.findFileByPath(this, path) else null
    }

    override fun refresh(asynchronous: Boolean) {
        VfsImplUtil.refresh(this, asynchronous)
    }

    override fun refreshAndFindFileByPath(path: String): VirtualFile? {
        return if (isValid(path)) VfsImplUtil.refreshAndFindFileByPath(this, path) else null
    }

    override fun normalize(path: String): String {
        val p = path.indexOf(SEPARATOR)
        return if (p > 0) FileUtil.normalize(path.substring(0, p)) + path.substring(p) else path
    }
}