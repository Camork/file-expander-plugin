package com.github.camork.action


import com.intellij.ide.highlighter.ArchiveFileType
import com.intellij.ide.highlighter.JavaClassFileType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.fileEditor.impl.LoadTextUtil
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.ArchiveFileSystem
import org.apache.commons.io.FilenameUtils
import org.jetbrains.annotations.NotNull

import java.nio.file.Files
import java.nio.file.Path

class DecompileJarAction extends AnAction {

    @Override
    void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(
                e.getData(PlatformDataKeys.VIRTUAL_FILE)?.getFileType() == ArchiveFileType.INSTANCE
        )
    }

    @Override
    void actionPerformed(@NotNull AnActionEvent e) {
        def file = e.getData(PlatformDataKeys.VIRTUAL_FILE)
        def fileSystem = file.getFileSystem()
        if (fileSystem instanceof ArchiveFileSystem) {
            def localVf = fileSystem.getLocalByEntry(file)
            def localFile = localVf.toNioPath()

            ProgressManager.getInstance().run(new Task.Modal(e.getProject(), "Decompiling jar", true) {

                @Override
                void run(@NotNull ProgressIndicator indicator) {
                    Path.of(FilenameUtils.removeExtension(localFile.toString())).with {
                        visit(it, file)
                    }
                    localVf.parent.refresh(false, true)
                }
            })
        }
    }

    void visit(Path dir, VirtualFile parent) {
        dir.toFile().mkdirs()
        parent.children.each { child ->
            if (child.directory) {
                visit(dir.resolve(child.name), child)
            }
            else if (child.fileType == JavaClassFileType.INSTANCE) {
                def text = LoadTextUtil.loadText(child)
                def classFile = dir.resolve(child.getName())
                def javaFileName = com.google.common.io.Files.getNameWithoutExtension(classFile.getFileName().toString()).concat(".java")
                classFile.parent.resolve(javaFileName).toFile().with {
                    it.createNewFile()
                    it.write(text as String)
                }
            }
            else {
                dir.resolve(child.getName()).with {
                    Files.copy(child.getInputStream(), it)
                }
            }
        }
    }

}
