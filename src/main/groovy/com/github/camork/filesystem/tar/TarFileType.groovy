package com.github.camork.filesystem.tar

import com.github.camork.filesystem.ArchiveBasedFileType
import com.intellij.openapi.fileTypes.FileType

/**
 * @author Charles Wu
 */
class TarFileType extends ArchiveBasedFileType {

    public static final FileType INSTANCE = new TarFileType()

    @Override
    String getName() {
        return 'TAR'
    }

    @Override
    String getDefaultExtension() {
        return 'tar'
    }

}