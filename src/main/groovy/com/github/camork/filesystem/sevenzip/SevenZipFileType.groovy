package com.github.camork.filesystem.sevenzip

import com.github.camork.filesystem.ArchiveBasedFileType
import com.intellij.openapi.fileTypes.FileType

/**
 * @author Charles Wu
 */
class SevenZipFileType extends ArchiveBasedFileType {

    public static final FileType INSTANCE = new SevenZipFileType()

    @Override
    String getName() {
        return '7ZIP'
    }

    @Override
    String getDefaultExtension() {
        return '7z'
    }

}