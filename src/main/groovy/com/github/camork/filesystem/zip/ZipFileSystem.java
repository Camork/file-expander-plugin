// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.github.camork.filesystem.zip;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.impl.jar.JarFileSystemImpl;

import org.jetbrains.annotations.NotNull;

public abstract class ZipFileSystem extends JarFileSystemImpl {

    public static final String protocol = "zip";

    public static ZipFileSystem getInstance() {
        return (ZipFileSystem)VirtualFileManager.getInstance().getFileSystem(protocol);
    }

    @Override
    protected boolean isCorrectFileType(@NotNull VirtualFile local) {
        return true;
    }

}
