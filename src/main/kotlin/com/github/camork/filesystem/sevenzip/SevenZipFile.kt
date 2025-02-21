package com.github.camork.filesystem.sevenzip

import com.github.camork.filesystem.IArchiveFile
import com.github.camork.util.ArchiveUtils
import com.github.camork.util.EntryInfo
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import java.io.File
import java.io.IOException

/**
 * @author Charles Wu
 */
class SevenZipFile(
    private val _file: File
) : IArchiveFile {

    private var entries: Map<String, ArchiveEntry>? = null

    override fun createEntriesInfoMap(): Map<String, EntryInfo> {
        val entries = calculateEntries() ?: return emptyMap()
        return ArchiveUtils.buildEntryMap(entries)
    }

    override fun getEntryBytes(relativePath: String): ByteArray {
        val entries = calculateEntries() ?: return ByteArray(0)
        val item = entries[relativePath] ?: return ByteArray(0)

        return SevenZFile.Builder()
            .setFile(_file)
            .get()
            .use {
                return ArchiveUtils.getEntryBytes(it, item)
            }
    }

    override fun getInputStream(): ArchiveInputStream<SevenZArchiveEntry>? = null

    override fun close() {
    }

    private fun calculateEntries(): Map<String, ArchiveEntry>? {
        if (entries == null) {
            try {
                SevenZFile.Builder()
                    .setFile(_file)
                    .get()
                    .use {
                        entries = ArchiveUtils.calculateEntries(it)
                    }
            } catch (_: IOException) {
                return null
            }
        }
        return entries
    }
}