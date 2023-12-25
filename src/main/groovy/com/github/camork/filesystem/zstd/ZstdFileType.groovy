package com.github.camork.filesystem.zstd

import com.github.camork.filesystem.ArchiveBasedFileType
import com.intellij.openapi.fileTypes.FileType

class ZstdFileType extends ArchiveBasedFileType {

    public static final FileType INSTANCE = new ZstdFileType()

    @Override
    String getName() {
        return 'ZSTANDARD'
    }

    @Override
    String getDefaultExtension() {
        return 'zst'
    }
}
