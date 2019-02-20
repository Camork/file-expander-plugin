package com.github.camork.extensions

import com.github.camork.filesystem.ArchiveBasedFileSystem
import com.github.camork.filesystem.gz.GZFileSystem
import com.github.camork.filesystem.gz.GZFileType
import com.github.camork.filesystem.tar.TarFileSystem
import com.github.camork.filesystem.tar.TarFileType
import com.github.camork.nodes.ArchiveBasedPsiNode
import com.github.camork.util.CoreUtil
import com.intellij.ide.highlighter.ArchiveFileType
import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.JarFileSystem
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import org.jetbrains.annotations.NotNull

import java.nio.file.Files

/**
 * @author Charles Wu
 */
class ArchiveTreeProvider implements TreeStructureProvider {

    @NotNull
    @Override
    Collection<AbstractTreeNode> modify(@NotNull AbstractTreeNode parent,
                                        @NotNull Collection<AbstractTreeNode> children,
                                        ViewSettings settings) {
        return children.collect {
            if (it instanceof PsiFileNode && it.virtualFile?.isValid()) {
                Project project = parent.project

                VirtualFile treeNodeFile = it.virtualFile

                if (treeNodeFile == null) {
                    return it
                }

                // copy the nested file into temporary folder if it should be.
                if (ArchiveBasedFileSystem.isNestedFile(treeNodeFile.path)) {
                    File targetFile = CoreUtil.getTempFilePath(project, treeNodeFile).toFile()

                    if (!targetFile.exists()) {
                        targetFile.parentFile.mkdirs()

                        Files.copy(treeNodeFile.getInputStream(), targetFile.toPath())
                    }

                    VirtualFile targetVf = LocalFileSystem.getInstance().findFileByIoFile(targetFile)

                    if (targetVf != null) {
                        treeNodeFile = targetVf
                    }
                }

                VirtualFile archiveFile

                FileType fileType = treeNodeFile.fileType

                switch (fileType) {
                    case ArchiveFileType.INSTANCE:
                        archiveFile = JarFileSystem.getInstance().getRootByLocal(treeNodeFile)
                        break
                    case GZFileType.INSTANCE:
                        archiveFile = GZFileSystem.getInstance().getRootByLocal(treeNodeFile)
                        break
                    case TarFileType.INSTANCE:
                        archiveFile = TarFileSystem.getInstance().getRootByLocal(treeNodeFile)
                }

                if (archiveFile != null) {
                    final PsiManager psiManager = PsiManager.getInstance(project)
                    final PsiDirectory psiDir = psiManager.findDirectory(archiveFile)

                    return psiDir != null
                            ? new ArchiveBasedPsiNode(project, psiDir, archiveFile, settings)
                            : it
                }
            }

            return it
        }
    }

}