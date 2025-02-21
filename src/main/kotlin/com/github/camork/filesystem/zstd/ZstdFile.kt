package com.github.camork.filesystem.zstd

import com.github.camork.filesystem.IArchiveFile
import com.github.camork.util.ArchiveUtils
import com.github.camork.util.EntryInfo
import io.airlift.compress.zstd.ZstdInputStream
import java.io.File
import java.io.InputStream

/**
 * @author Charles Wu
 */
class ZstdFile(
    private val file: File
) : IArchiveFile {

    private var zstdInputStream: ZstdInputStream? = null

    override fun createEntriesInfoMap(): Map<String, EntryInfo> {
        val entries = mutableMapOf<String, EntryInfo>()
        val root = ArchiveUtils.createRootEntry()
        entries[""] = root

        val entryName = file.name.replace(".zst", "")
        entries[entryName] = EntryInfo(entryName, false, file.length(), file.lastModified(), root)

        return entries
    }

    override fun getEntryBytes(relativePath: String): ByteArray? {
        return getInputStream()?.use { it.readBytes() }
    }

    override fun getInputStream(): InputStream? {
        return ZstdInputStream(file.inputStream()).also { zstdInputStream = it }
    }

    override fun close() {
        zstdInputStream?.close()
    }
}