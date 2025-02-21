package com.github.camork.extensions

import com.github.camork.filesystem.ArchiveBasedFileSystem
import com.github.camork.filesystem.MyArchiveFileType
import com.github.camork.filesystem.gz.GZFileSystem
import com.github.camork.filesystem.gz.GZFileType
import com.github.camork.filesystem.sevenzip.SevenZipFileSystem
import com.github.camork.filesystem.sevenzip.SevenZipFileType
import com.github.camork.filesystem.tar.TarFileSystem
import com.github.camork.filesystem.tar.TarFileType
import com.github.camork.filesystem.tar.TarGzFileType
import com.github.camork.filesystem.zip.ZipFileSystem
import com.github.camork.filesystem.zstd.ZstdFileSystem
import com.github.camork.filesystem.zstd.ZstdFileType
import com.github.camork.nodes.PsiDirectoryNodeWrapper
import com.github.camork.util.CoreUtil
import com.intellij.ide.highlighter.ArchiveFileType
import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Files

/**
 * @author Charles Wu
 */
class ArchiveTreeProvider : TreeStructureProvider {

    override fun modify(
        parent: AbstractTreeNode<*>,
        children: Collection<AbstractTreeNode<*>>,
        settings: ViewSettings
    ): Collection<AbstractTreeNode<*>> {
        return children.map { it ->
            if (it is PsiFileNode && it.virtualFile?.isValid == true) {
                val project = parent.project
                var treeNodeFile = it.virtualFile

                if (treeNodeFile == null) return@map it
                var isNestedFile = false

                // copy the nested file into temporary folder if it should be.
                if (ArchiveBasedFileSystem.isNestedFile(treeNodeFile.path)) {
                    val targetFile = CoreUtil.getTempFilePath(treeNodeFile).toFile()
                    if (!targetFile.exists()) {
                        targetFile.parentFile.mkdirs()
                        treeNodeFile.inputStream.use { input ->
                            Files.copy(input, targetFile.toPath())
                        }
                    }
                    val targetVf = LocalFileSystem.getInstance().findFileByIoFile(targetFile)
                    if (targetVf != null) {
                        treeNodeFile = targetVf
                        isNestedFile = true
                    }
                }

                val archiveFile: VirtualFile? = when (treeNodeFile.fileType) {
                    ArchiveFileType.INSTANCE, MyArchiveFileType -> ZipFileSystem.getInstance().getRootByLocal(treeNodeFile)
                    GZFileType, TarGzFileType -> GZFileSystem.getInstance().getRootByLocal(treeNodeFile)
                    TarFileType -> TarFileSystem.getInstance().getRootByLocal(treeNodeFile)
                    SevenZipFileType -> SevenZipFileSystem.getInstance().getRootByLocal(treeNodeFile)
                    ZstdFileType -> ZstdFileSystem.getInstance().getRootByLocal(treeNodeFile)
                    else -> null
                }

                if (archiveFile != null) {
                    return@map PsiDirectoryNodeWrapper(project, it.value, settings, isNestedFile, archiveFile)
                }
            }

            return@map it
        }
    }
}