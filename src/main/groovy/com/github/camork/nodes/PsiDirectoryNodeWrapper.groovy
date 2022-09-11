package com.github.camork.nodes

import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.PsiManagerImpl
import com.intellij.psi.impl.file.PsiDirectoryImpl
import org.jetbrains.annotations.NotNull

/**
 * @author Charles Wu
 */
class PsiDirectoryNodeWrapper extends PsiFileNode {

    private final VirtualFile _archiveFile
    private final boolean _isNestedFile

    PsiDirectoryNodeWrapper(Project project, @NotNull PsiFile value, ViewSettings viewSettings, boolean isNestedFile, VirtualFile archiveFile) {
        super(project, value, viewSettings)
        this._isNestedFile = isNestedFile
        this._archiveFile = archiveFile
    }

    @Override
    Collection<AbstractTreeNode> getChildrenImpl() {
        final PsiManager psiManager = PsiManager.getInstance(project)
        PsiDirectory psiDir
        if (_isNestedFile) {
            psiDir = new PsiDirectoryImpl(psiManager as PsiManagerImpl, _archiveFile)
        } else {
            psiDir = psiManager.findDirectory(_archiveFile)
        }

        def list = new ArrayList<AbstractTreeNode>()
        if (psiDir != null) {
            list.addAll(new ArchiveBasedPsiNode(project, psiDir, _archiveFile, settings).getChildren())
        }
        return list
    }

}