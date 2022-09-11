package com.github.camork.nodes

import com.google.common.base.MoreObjects
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import org.jetbrains.annotations.NotNull

/**
 * @author Charles Wu
 */
class ArchiveBasedPsiNode extends PsiDirectoryNode {

    private final VirtualFile _archiveFile

    ArchiveBasedPsiNode(Project project, @NotNull PsiDirectory value,
                        VirtualFile jarFile, ViewSettings viewSettings) {
        super(project, value, viewSettings)
        _archiveFile = jarFile
    }

    @Override
    Collection<AbstractTreeNode> getChildrenImpl() {
        final PsiManager psiManager = PsiManager.getInstance(project)

        VirtualFile[] virtualFiles = _archiveFile.children
        Collection<AbstractTreeNode> children = new ArrayList<>(virtualFiles.length)
        for (file in virtualFiles) {
            if (!file.isValid()) {
                continue
            }

            if (file.isDirectory()) {
                final PsiDirectory psiDir = psiManager.findDirectory(file)
                if (psiDir != null) {
                    children.add(new ArchiveBasedPsiNode(project, psiDir, file, settings))
                }
            }
            else {
                final PsiFile psiFile = psiManager.findFile(file)
                if (psiFile != null) {
                    children.add(new PsiFileNode(project, psiFile, settings))
                }
            }
        }

        return children
    }

    @Override
    boolean isValid() {
        return true
    }


    @Override
    String toString() {
        return MoreObjects.toStringHelper(this)
                .add("_archiveFile", _archiveFile)
                .toString()
    }
}