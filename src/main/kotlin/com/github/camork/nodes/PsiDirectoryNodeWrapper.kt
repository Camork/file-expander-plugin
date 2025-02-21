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

/**
 * @author Charles Wu
 */
class PsiDirectoryNodeWrapper(
    project: Project,
    value: PsiFile,
    viewSettings: ViewSettings,
    private val isNestedFile: Boolean,
    private val archiveFile: VirtualFile
) : PsiFileNode(project, value, viewSettings) {

    override fun getChildrenImpl(): Collection<AbstractTreeNode<*>> {
        val psiManager = PsiManager.getInstance(project)
        val psiDir: PsiDirectory? = if (isNestedFile) {
            PsiDirectoryImpl(psiManager as PsiManagerImpl, archiveFile)
        } else {
            psiManager.findDirectory(archiveFile)
        }

        val list = mutableListOf<AbstractTreeNode<*>>()
        psiDir?.let {
            list.addAll(ArchiveBasedPsiNode(project, it, settings, archiveFile).children)
        }
        return list
    }
}