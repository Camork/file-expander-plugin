package com.github.camork.filesystem.gz

import com.intellij.openapi.util.io.FileAttributes
import com.intellij.openapi.util.io.FileSystemUtil
import com.intellij.openapi.vfs.impl.ArchiveHandler
import com.intellij.openapi.vfs.impl.ArchiveHandler.EntryInfo
import com.intellij.util.io.FileAccessorCache
import com.intellij.util.io.ResourceHandle
import org.jetbrains.annotations.NotNull

/**
 * @author Charles Wu
 */
class GZFileHandler extends ArchiveHandler {

    private volatile String myCanonicalPathToZip
    private volatile long myFileStamp
    private volatile long myFileLength

    GZFileHandler(@NotNull String path) {
        super(path)
    }

    @Override
    protected Map<String, EntryInfo> createEntriesMap() throws IOException {
        def gZFile = acquireGZipHandle().get()

        Map entries = [:]

        def root = new EntryInfo('', true, DEFAULT_LENGTH, DEFAULT_TIMESTAMP, null)
        entries.put('', root)
        entries.put(gZFile.name, new EntryInfo(gZFile.name, false, gZFile.length, gZFile.lastModified, root))

        return entries
    }

    @Override
    byte[] contentsToByteArray(@NotNull String relativePath) throws IOException {
        def gZFile = acquireGZipHandle().get()
        return gZFile.getBytes()
    }

    @NotNull
    private String getCanonicalPath() throws IOException {
        String value = myCanonicalPathToZip

        if (value == null) {
            myCanonicalPathToZip = value = file.getCanonicalPath()
        }

        return value
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

        }
    }

    @NotNull
    protected ResourceHandle<GZFile> acquireGZipHandle() throws IOException {
        return getCachedGZFileHandle(true)
    }

    private FileAccessorCache.Handle<GZFile> getCachedGZFileHandle(boolean createIfNeeded) throws IOException {
        try {
            FileAccessorCache.Handle<GZFile> handle = createIfNeeded ? fileAccessor.get(this) : fileAccessor.getIfCached(this)

            if (handle != null) {
                FileAttributes attributes = FileSystemUtil.getAttributes(canonicalPath)
                if (attributes == null) {
                    throw new FileNotFoundException(file.canonicalFile.toString())
                }

                if (attributes.lastModified == myFileStamp && attributes.length == myFileLength) {
                    return handle
                }

                clearCaches()
                handle.release()
                handle = fileAccessor.get(this)
            }

            return handle
        }
        catch (RuntimeException e) {
            Throwable cause = e.getCause()
            if (cause instanceof IOException) throw (IOException) cause
            throw e
        }
    }

    static synchronized void setFileAttributes(@NotNull GZFileHandler gzFileHandler, @NotNull String path) {
        FileAttributes attributes = FileSystemUtil.getAttributes(path)

        gzFileHandler.myFileStamp = attributes != null ? attributes.lastModified : DEFAULT_TIMESTAMP
        gzFileHandler.myFileLength = attributes != null ? attributes.length : DEFAULT_LENGTH
    }

    @Override
    protected void clearCaches() {
        fileAccessor.remove(this)
        super.clearCaches()
    }

}