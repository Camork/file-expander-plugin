package com.github.camork.filesystem.sevenzip

import com.github.camork.filesystem.ArchiveHandlerBase
import com.intellij.util.io.FileAccessorCache
import org.jetbrains.annotations.NotNull

/**
 * @author Charles Wu
 */
class SevenZipFileHandler extends ArchiveHandlerBase<SevenZipFile> {

    SevenZipFileHandler(@NotNull String path) {
        super(path)
    }

    @Override
    FileAccessorCache<SevenZipFileHandler, SevenZipFile> getFileAccessor() {
        fileAccessor
    }

    private static final FileAccessorCache<SevenZipFileHandler, SevenZipFile> fileAccessor =
            new FileAccessorCache<SevenZipFileHandler, SevenZipFile>(20, 10) {

                @Override
                protected SevenZipFile createAccessor(SevenZipFileHandler key) throws IOException {
                    setFileAttributes(key, key.canonicalPath)
                    return new SevenZipFile(key.file.canonicalFile)
                }

                @Override
                boolean isEqual(SevenZipFileHandler val1, SevenZipFileHandler val2) {
                    return val1 == val2
                }

                @Override
                protected void disposeAccessor(@NotNull SevenZipFile fileAccessor) throws IOException {
                    fileAccessor.close()
                }

            }

}