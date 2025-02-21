package com.github.camork.action

import com.intellij.ide.highlighter.ArchiveFileType
import com.intellij.ide.highlighter.JavaClassFileType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.fileEditor.impl.LoadTextUtil
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.vfs.JarFileSystem
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.openapi.vfs.newvfs.ArchiveFileSystem
import com.intellij.openapi.vfs.newvfs.impl.FsRoot
import org.apache.commons.io.FilenameUtils
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Charles Wu
 */
class DecompileJarAction : AnAction() {

    override fun update(e: AnActionEvent) {
        val file = e.getData(PlatformDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabledAndVisible = file?.fileType == ArchiveFileType.INSTANCE && file.extension == "jar"
    }

    override fun actionPerformed(e: AnActionEvent) {
        var file: VirtualFile? = e.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return

        file = JarFileSystem.getInstance().getRootByLocal(file!!)

        if (file == null) {
            return
        }
        val fileSystem: ArchiveFileSystem = file.fileSystem as ArchiveFileSystem

        val localVf = fileSystem.getLocalByEntry(file)
        val localFile = localVf?.toNioPath()

        ProgressManager.getInstance().run(object : Task.Modal(e.project, "Decompiling jar", true) {
            override fun run(indicator: ProgressIndicator) {
                val decompileDir = Path.of(FilenameUtils.removeExtension(localFile.toString()))
                visit(decompileDir, file)
                localVf?.parent?.refresh(false, true)
            }
        })
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    private fun visit(dir: Path, parent: VirtualFile) {
        VfsUtilCore.visitChildrenRecursively(parent, object : VirtualFileVisitor<Void>() {
            override fun visitFile(child: VirtualFile): Boolean {
                if (child is FsRoot) {
                    return true
                } else if (child.isDirectory) {
                    val subDir = dir.resolve(child.name)
                    subDir.toFile().mkdirs()
                    return true
                } else if (child.fileType == JavaClassFileType.INSTANCE) {
                    val text = LoadTextUtil.loadText(child)
                    val classFile = dir.resolve(child.name)
                    val javaFileName = com.google.common.io.Files.getNameWithoutExtension(classFile.fileName.toString()).plus(".java")
                    classFile.parent.resolve(javaFileName).toFile().apply {
                        createNewFile()
                        writeText(text.toString())
                    }
                } else {
                    dir.resolve(child.name).let {
                        Files.copy(child.inputStream, it)
                    }
                }
                return true
            }
        })
    }
}