package com.github.camork.filesystem.tar

import com.github.camork.filesystem.ArchiveBasedFileType
import com.intellij.openapi.fileTypes.FileType

/**
 * @author Charles Wu
 */
class TarGzFileType extends ArchiveBasedFileType {

    public static final FileType INSTANCE = new TarGzFileType()

    @Override
    String getName() {
        return 'TGZ'
    }

    @Override
    String getDefaultExtension() {
        return 'tgz'
    }

}