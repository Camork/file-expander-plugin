package com.github.camork.filesystem.gz

import com.github.camork.filesystem.ArchiveHandlerBase
import com.intellij.util.io.FileAccessorCache
import org.jetbrains.annotations.NotNull

/**
 * @author Charles Wu
 */
class GZFileHandler extends ArchiveHandlerBase<GZFile> {

    GZFileHandler(@NotNull String path) {
        super(path)
    }

    @Override
    FileAccessorCache<GZFileHandler, GZFile> getFileAccessor() {
        fileAccessor
    }

    private static final FileAccessorCache<GZFileHandler, GZFile> fileAccessor = new FileAccessorCache<GZFileHandler, GZFile>(20, 10) {

        @Override
        protected GZFile createAccessor(GZFileHandler key) throws IOException {
            setFileAttributes(key, key.canonicalPath)
            return new GZFile(key.file.canonicalFile)
        }

        @Override
        boolean isEqual(GZFileHandler val1, GZFileHandler val2) {
            return val1 == val2
        }

        @Override
        protected void disposeAccessor(@NotNull GZFile fileAccessor) throws IOException {
            fileAccessor.close()
        }

    }

}