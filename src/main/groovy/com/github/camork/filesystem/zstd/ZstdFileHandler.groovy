package com.github.camork.filesystem.zstd

import com.github.camork.filesystem.ArchiveHandlerBase
import com.intellij.util.io.FileAccessorCache
import org.jetbrains.annotations.NotNull

class ZstdFileHandler extends ArchiveHandlerBase<ZstdFile> {

    ZstdFileHandler(@NotNull String path) {
        super(path)
    }

    @Override
    FileAccessorCache<ZstdFileHandler, ZstdFile> getFileAccessor() {
        fileAccessor
    }

    private static final FileAccessorCache<ZstdFileHandler, ZstdFile> fileAccessor =
            new FileAccessorCache<ZstdFileHandler, ZstdFile>(20, 10) {

            @Override
            protected ZstdFile createAccessor(ZstdFileHandler key) throws IOException {
                setFileAttributes(key, key.canonicalPath)
                return new ZstdFile(key.file.canonicalFile)
            }

            @Override
            boolean isEqual(ZstdFileHandler val1, ZstdFileHandler val2) {
                return val1 == val2
            }

            @Override
            protected void disposeAccessor(@NotNull ZstdFile fileAccessor) throws IOException {
                fileAccessor.close()
            }
        }
}