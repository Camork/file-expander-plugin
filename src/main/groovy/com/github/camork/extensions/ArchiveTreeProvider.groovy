package com.github.camork.extensions

import com.github.camork.filesystem.gz.GZFileSystem
import com.github.camork.filesystem.gz.GZFileType
import com.github.camork.filesystem.tar.TarFileSystem
import com.github.camork.filesystem.tar.TarFileType
import com.github.camork.nodes.ArchiveBasedPsiNode
import com.intellij.ide.highlighter.ArchiveFileType
import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.JarFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import org.jetbrains.annotations.NotNull

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
                FileType fileType = it.virtualFile?.fileType

                VirtualFile localFile
                if (fileType == ArchiveFileType.INSTANCE) {
                    localFile = JarFileSystem.getInstance().getRootByLocal(it.virtualFile)
                }
                else if (fileType == GZFileType.INSTANCE) {
                    localFile = GZFileSystem.getInstance().getRootByLocal(it.virtualFile)
                }
                else if (fileType == TarFileType.INSTANCE) {
                    localFile = TarFileSystem.getInstance().getRootByLocal(it.virtualFile)
                }

                if (localFile != null) {
                    final PsiManager psiManager = PsiManager.getInstance(parent.project)
                    final PsiDirectory psiDir = psiManager.findDirectory(localFile)

                    return psiDir != null
                            ? new ArchiveBasedPsiNode(parent.project, psiDir, localFile, settings)
                            : it
                }
            }

            return it
        }
    }

}