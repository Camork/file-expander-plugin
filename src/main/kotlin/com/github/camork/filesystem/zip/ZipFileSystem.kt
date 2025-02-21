package com.github.camork.filesystem.zip

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.impl.jar.JarFileSystemImpl

/**
 * @author Charles Wu
 */
abstract class ZipFileSystem : JarFileSystemImpl() {

    companion object {
        const val PROTOCOL = "zip"

        fun getInstance(): ZipFileSystem {
            return VirtualFileManager.getInstance().getFileSystem(PROTOCOL) as ZipFileSystem
        }
    }

    override fun isCorrectFileType(local: VirtualFile): Boolean {
        return true
    }
}

class ZipFileSystemImpl : ZipFileSystem() {

    override fun getProtocol(): String = PROTOCOL
}