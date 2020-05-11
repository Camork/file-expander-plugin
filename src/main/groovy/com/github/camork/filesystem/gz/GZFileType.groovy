package com.github.camork.filesystem.gz

import com.github.camork.filesystem.ArchiveBasedFileType
import com.intellij.openapi.fileTypes.FileType

/**
 * @author Charles Wu
 */
class GZFileType extends ArchiveBasedFileType {

    public static final FileType INSTANCE = new GZFileType()

    @Override
    String getName() {
        return 'GZIP'
    }

    @Override
    String getDefaultExtension() {
        return 'gz'
    }

}