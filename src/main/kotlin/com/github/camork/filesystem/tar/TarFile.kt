package com.github.camork.filesystem.tar

import com.github.camork.filesystem.IArchiveFile
import com.github.camork.util.ArchiveUtils
import com.github.camork.util.EntryInfo
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.io.File
import java.io.FileInputStream

/**
 * @author Charles Wu
 */
open class TarFile(protected val _file: File) : IArchiveFile {

    protected var _fileInputStream: FileInputStream? = null
    protected var entries: Map<String, ArchiveEntry>? = null

    override fun createEntriesInfoMap(): Map<String, EntryInfo> {
        return getInputStream().use {
            ArchiveUtils.buildEntryMap(calculateEntries())
        }
    }

    override fun getEntryBytes(relativePath: String): ByteArray? {
        val item = calculateEntries()[relativePath] ?: return ByteArray(0)
        return getInputStream()?.use { it: ArchiveInputStream<TarArchiveEntry> ->
            ArchiveUtils.getEntryBytes(it, item)
        }
    }

    override fun getInputStream(): ArchiveInputStream<TarArchiveEntry>? {
        _fileInputStream = FileInputStream(_file)
        return TarArchiveInputStream(_fileInputStream)
    }

    override fun close() {
        _fileInputStream?.close()
    }

    private fun calculateEntries(): Map<String, ArchiveEntry> {
        if (entries == null) {
            entries = getInputStream()?.use {
                ArchiveUtils.calculateEntries(it)
            }
        }
        return entries!!
    }
}