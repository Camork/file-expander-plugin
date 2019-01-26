package com.github.camork.filesystem.tar

import com.github.camork.filesystem.ArchiveHandlerBase
import com.intellij.util.io.FileAccessorCache
import org.jetbrains.annotations.NotNull

/**
 * @author Charles Wu
 */
class TarFileHandler extends ArchiveHandlerBase<TarFile> {

    TarFileHandler(@NotNull String path) {
        super(path)
    }

    @Override
    protected Map<String, ?> createEntriesMap() throws IOException {
        return acquireFileHandle().get().createEntriesInfoMap()
    }

    @Override
    byte[] contentsToByteArray(@NotNull String relativePath) throws IOException {
        return acquireFileHandle().get().getEntryBytes(relativePath)
    }

    @Override
    FileAccessorCache getFileAccessor() {
        return fileAccessor
    }

    private static final FileAccessorCache<TarFileHandler, TarFile> fileAccessor = new FileAccessorCache<TarFileHandler, TarFile>(20, 10) {

        @Override
        protected TarFile createAccessor(TarFileHandler key) throws IOException {
            setFileAttributes(key, key.canonicalPath)
            return new TarFile(key.file.canonicalFile)
        }

        @Override
        boolean isEqual(TarFileHandler val1, TarFileHandler val2) {
            return val1 == val2
        }

        @Override
        protected void disposeAccessor(@NotNull TarFile fileAccessor) throws IOException {
            fileAccessor.closeStream()
        }

    }

}