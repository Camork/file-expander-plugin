package com.github.camork.extensions

import com.github.camork.nodes.ArchiveBasedPsiNode
import com.intellij.ide.highlighter.ArchiveFileType
import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode
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
			if (it instanceof PsiFileNode &&
					it.virtualFile?.isValid() &&
					it.virtualFile?.fileType == ArchiveFileType.INSTANCE) {

				VirtualFile jarFile = JarFileSystem.getInstance().getJarRootForLocalFile(it.virtualFile)

				if (jarFile != null) {
					final PsiManager psiManager = PsiManager.getInstance(parent.project)
					final PsiDirectory psiDir = psiManager.findDirectory(jarFile)

					return psiDir != null
							? new ArchiveBasedPsiNode(parent.project, psiDir, jarFile, settings)
							: it
				}
			}
			return it
		}
	}

}
