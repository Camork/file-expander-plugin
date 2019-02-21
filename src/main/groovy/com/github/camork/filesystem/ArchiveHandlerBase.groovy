package com.github.camork.filesystem

import com.github.camork.util.ArchiveUtils
import com.intellij.openapi.util.io.FileAttributes
import com.intellij.openapi.util.io.FileSystemUtil
import com.intellij.openapi.vfs.impl.ArchiveHandler
import com.intellij.util.io.FileAccessorCache
import com.intellij.util.io.ResourceHandle
import org.jetbrains.annotations.NotNull

/**
 * @author Charles Wu
 */
abstract class ArchiveHandlerBase<T extends IArchiveFile> extends ArchiveHandler {

    protected volatile String canonicalPathToFile
    protected volatile long _fileStamp
    protected volatile long _fileLength

    protected ArchiveHandlerBase(@NotNull String path) {
        super(path)
    }

    @NotNull
    String getCanonicalPath() throws IOException {
        if (canonicalPathToFile == null) {
            canonicalPathToFile = file.getCanonicalPath()
        }

        return canonicalPathToFile
    }

    /**
     * Use a FileAccessorCache to avoid the expensive explore
     */
    abstract FileAccessorCache<ArchiveHandlerBase, T> getFileAccessor()

    @NotNull
    protected ResourceHandle<T> acquireFileHandle() throws IOException {
        return getCachedFileHandle(true)
    }

    @Override
    protected Map<String, ?> createEntriesMap() throws IOException {
        return acquireFileHandle().get().createEntriesInfoMap()
    }

    @Override
    byte[] contentsToByteArray(@NotNull String relativePath) throws IOException {
        return acquireFileHandle().get().getEntryBytes(relativePath)
    }

    private FileAccessorCache.Handle<T> getCachedFileHandle(boolean createIfNeeded) throws IOException {
        try {
            FileAccessorCache.Handle<T> handle = createIfNeeded ? fileAccessor.get(this) : fileAccessor.getIfCached(this)

            if (handle != null) {
                FileAttributes attributes = FileSystemUtil.getAttributes(canonicalPath)
                if (attributes == null) {
                    throw new FileNotFoundException(file.canonicalFile.toString())
                }

                if (attributes.lastModified == _fileStamp && attributes.length == _fileLength) {
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

    static synchronized void setFileAttributes(@NotNull ArchiveHandlerBase handler, @NotNull String path) {
        FileAttributes attributes = FileSystemUtil.getAttributes(path)

        handler._fileStamp = attributes != null ? attributes.lastModified : ArchiveUtils.DEFAULT_TIMESTAMP
        handler._fileLength = attributes != null ? attributes.length : ArchiveUtils.DEFAULT_LENGTH
    }

    @Override
    protected void clearCaches() {
        fileAccessor.remove(this)
        super.clearCaches()
    }

}