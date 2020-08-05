package com.github.camork.filesystem;

import org.jetbrains.annotations.NotNull;

public class MyArchiveFileType extends com.intellij.ide.highlighter.ArchiveFileType {

    public static final MyArchiveFileType INSTANCE = new MyArchiveFileType();

    @NotNull
    @Override
    public String getName() {
        return "ARCHIVE(extend)";
    }
}
