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
import com.intellij.psi.impl.PsiManagerImpl
import com.intellij.psi.impl.file.PsiDirectoryImpl
import org.jetbrains.annotations.NotNull

import java.nio.file.Files

/**
 * @author Charles Wu
 */
class ArchiveTreeProvider implements TreeStructureProvider {

    private static String[] archiveExtensions = ["zip", "jar", "war", "ear", "swc", "ane", "egg", "apk"]

    private static String[] archiveExtendExtensions = ["epc"]

    @NotNull
    @Override
    Collection<AbstractTreeNode<?>> modify(@NotNull AbstractTreeNode<?> parent,
                                        @NotNull Collection<AbstractTreeNode<?>> children,
                                        ViewSettings settings) {
        return children.collect {
            if (it instanceof PsiFileNode && it.virtualFile?.isValid()) {
                Project project = parent.project

                VirtualFile treeNodeFile = it.virtualFile

                if (treeNodeFile == null) {
                    return it
                }

                boolean isNestedFile = false

                // copy the nested file into temporary folder if it should be.
                if (ArchiveBasedFileSystem.isNestedFile(treeNodeFile.path)) {
                    File targetFile = CoreUtil.getTempFilePath(treeNodeFile).toFile()

                    if (!targetFile.exists()) {
                        targetFile.parentFile.mkdirs()

                        Files.copy(treeNodeFile.getInputStream(), targetFile.toPath())
                    }

                    VirtualFile targetVf = LocalFileSystem.getInstance().findFileByIoFile(targetFile)

                    if (targetVf != null) {
                        treeNodeFile = targetVf
                        isNestedFile = true
                    }
                }

                VirtualFile archiveFile

                FileType fileType = treeNodeFile.fileType

                switch (fileType) {
                    case ArchiveFileType.INSTANCE:
                        def extension = treeNodeFile.getExtension()
                        if (extension in archiveExtensions) {
                            archiveFile = JarFileSystem.getInstance().getRootByLocal(treeNodeFile)
                        }
                        break
                    case MyArchiveFileType.INSTANCE:
                        archiveFile = ZipFileSystem.getInstance().getRootByLocal(treeNodeFile)
                        break
                    case GZFileType.INSTANCE:
                    case TarGzFileType.INSTANCE:
                        archiveFile = GZFileSystem.getInstance().getRootByLocal(treeNodeFile)
                        break
                    case TarFileType.INSTANCE:
                        archiveFile = TarFileSystem.getInstance().getRootByLocal(treeNodeFile)
                        break
                    case SevenZipFileType.INSTANCE:
                        archiveFile = SevenZipFileSystem.getInstance().getRootByLocal(treeNodeFile)
                        break
                }

                if (archiveFile != null) {
                    final PsiManager psiManager = PsiManager.getInstance(project)
                    final PsiDirectory psiDir
                    if (isNestedFile) {
                        psiDir = new PsiDirectoryImpl(psiManager as PsiManagerImpl, archiveFile)
                    }
                    else {
                        psiDir = psiManager.findDirectory(archiveFile)
                    }

                    return psiDir != null
                            ? new ArchiveBasedPsiNode(project, psiDir, archiveFile, settings)
                            : it
                }
            }

            return it
        }
    }

}