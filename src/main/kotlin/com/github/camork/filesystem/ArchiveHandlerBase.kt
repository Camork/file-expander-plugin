package com.github.camork.filesystem

import com.github.camork.util.ArchiveUtils
import com.intellij.openapi.util.io.FileSystemUtil
import com.intellij.openapi.vfs.impl.ArchiveHandler
import com.intellij.util.io.FileAccessorCache
import com.intellij.util.io.ResourceHandle
import java.io.FileNotFoundException
import java.io.IOException

/**
 * @author Charles Wu
 */
abstract class ArchiveHandlerBase<T : IArchiveFile>(path: String) : ArchiveHandler(path) {

    @Volatile
    protected var canonicalPathToFile: String? = null
    @Volatile
    protected var _fileStamp: Long = 0
    @Volatile
    protected var _fileLength: Long = 0

    @Throws(IOException::class)
    fun getCanonicalPath(): String {
        if (canonicalPathToFile == null) {
            canonicalPathToFile = file.getCanonicalPath()
        }
        return canonicalPathToFile!!
    }

    /**
     * Use a FileAccessorCache to avoid the expensive exploring
     */
    abstract fun getFileAccessor(): FileAccessorCache<ArchiveHandlerBase<T>, T>

    @Throws(IOException::class)
    protected fun acquireFileHandle(): ResourceHandle<T> {
        return getCachedFileHandle(true)
    }

    @Synchronized
    @Throws(IOException::class)
    override fun createEntriesMap(): Map<String, EntryInfo> {
        return acquireFileHandle().use {
            val map: Map<String, com.github.camork.util.EntryInfo> = it.get().createEntriesInfoMap()

            //convert my map to the IntelliJ map
            val result = mutableMapOf<String, EntryInfo>()
            for ((key, value) in map) {
                convertEntry(key, value, map, result)
            }

            result
        }
    }

    private fun convertEntry(
        key: String,
        value: com.github.camork.util.EntryInfo,
        map: Map<String, com.github.camork.util.EntryInfo>,
        result: MutableMap<String, EntryInfo>
    ): EntryInfo {
        if (result.contains(key)) {
            return result[key]!!
        }
        val parentEntry: EntryInfo? = if (value.parent != null) {
            val parentKey = map.keys.first { map[it] == value.parent }
            var newParent = result[parentKey]
            if (newParent == null) {
                newParent = convertEntry(parentKey, map[parentKey]!!, map, result)
            }
            newParent
        } else null

        result[key] = EntryInfo(
            value.shortName,
            value.isDirectory,
            value.length,
            value.timestamp,
            parentEntry
        )

        return result[key]!!
    }

    @Throws(IOException::class)
    override fun contentsToByteArray(relativePath: String): ByteArray {
        val bytes = acquireFileHandle().get().getEntryBytes(relativePath)
        return bytes ?: ByteArray(0)
    }

    @Throws(IOException::class)
    private fun getCachedFileHandle(createIfNeeded: Boolean): FileAccessorCache.Handle<T> {
        try {
            var handle = if (createIfNeeded) getFileAccessor().get(this) else getFileAccessor().getIfCached(this)

            if (handle != null) {
                val attributes = FileSystemUtil.getAttributes(getCanonicalPath())
                if (attributes == null) {
                    throw FileNotFoundException(file.canonicalFile.toString())
                }

                if (attributes.lastModified == _fileStamp && attributes.length == _fileLength) {
                    return handle
                }

                clearCaches()
                handle.release()
                handle = getFileAccessor().get(this)
            }

            return handle
        } catch (e: RuntimeException) {
            val cause = e.cause
            if (cause is IOException) throw cause
            throw e
        }
    }

    companion object {

        @Synchronized
        fun setFileAttributes(handler: ArchiveHandlerBase<*>, path: String) {
            val attributes = FileSystemUtil.getAttributes(path)
            handler._fileStamp = attributes?.lastModified ?: ArchiveUtils.DEFAULT_TIMESTAMP
            handler._fileLength = attributes?.length ?: ArchiveUtils.DEFAULT_LENGTH
        }
    }

    override fun clearCaches() {
        getFileAccessor().remove(this)
        getFileAccessor().clear()
        super.clearCaches()
    }
}