package com.github.camork.filesystem

import com.github.camork.util.CoreUtil
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.ArchiveFileSystem
import com.intellij.openapi.vfs.newvfs.VfsImplUtil
import org.apache.commons.lang.StringUtils
import org.jetbrains.annotations.NotNull

/**
 * @author Charles Wu
 */
abstract class ArchiveBasedFileSystem extends ArchiveFileSystem {

    static final String SEPARATOR = '!/'

    static boolean isValid(String path) {
        return path.contains(separator)
    }

    static boolean isNestedFile(@NotNull String path) {
        if (StringUtils.countMatches(path, separator) > 0) {
            for (String fileExtension : CoreUtil.ARCHIVE_EXTENSIONS) {
                if (path.endsWith(fileExtension)) {
                    return true
                }
            }
        }

        return false
    }

    protected static String getSeparator() {
        SEPARATOR
    }

    @Override
    protected String composeRootPath(@NotNull String localPath) {
        return localPath + separator
    }

    @Override
    protected String extractLocalPath(@NotNull String rootPath) {
        return StringUtil.trimEnd(rootPath, separator)
    }

    @Override
    protected String extractRootPath(@NotNull String path) {
        final int jarSeparatorIndex = path.indexOf(separator)

        assert jarSeparatorIndex >= 0: "Path passed to ${this.class} must have separator '${separator}': " + path

        return path.substring(0, jarSeparatorIndex + separator.length())
    }

    @Override
    VirtualFile findFileByPathIfCached(@NotNull String path) {
        return isValid(path) ? VfsImplUtil.findFileByPathIfCached(this, path) : null
    }

    @Override
    VirtualFile findFileByPath(@NotNull String path) {
        return isValid(path) ? VfsImplUtil.findFileByPath(this, path) : null
    }

    @Override
    void refresh(boolean asynchronous) {
        VfsImplUtil.refresh(this, asynchronous)
    }

    @Override
    VirtualFile refreshAndFindFileByPath(@NotNull String path) {
        return isValid(path) ? VfsImplUtil.refreshAndFindFileByPath(this, path) : null
    }

    @NotNull
    @Override
    String normalize(@NotNull String path) {
        int p = path.indexOf(SEPARATOR)
        return p > 0 ? FileUtil.normalize(path.substring(0, p)) + path.substring(p) : super.normalize(path)
    }
}