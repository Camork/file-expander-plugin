package com.github.camork.filesystem.sevenzip

import com.github.camork.filesystem.IArchiveFile
import com.github.camork.util.ArchiveUtils
import com.github.camork.util.EntryInfo
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import org.jetbrains.annotations.Nullable

/**
 * @author Charles Wu
 */
class SevenZipFile implements IArchiveFile {

    private final File _file

    private Map<String, ArchiveEntry> entries

    SevenZipFile(File file) {
        _file = file
    }

    @Override
    Map<String, EntryInfo> createEntriesInfoMap() {
        def entries = calculateEntries()
        if (entries == null) {
            return [:]
        }
        return ArchiveUtils.buildEntryMap(entries)
    }

    @Override
    byte[] getEntryBytes(String relativePath) {
        def entries = calculateEntries()
        if (entries == null) {
            return new byte[0]
        }
        ArchiveEntry item = entries.get(relativePath)

        new SevenZFile(_file, (char[]) null).withCloseable {
            return ArchiveUtils.getEntryBytes(it, item)
        }
    }

    @Override
    ArchiveInputStream getInputStream() {
        return null
    }

    @Override
    void close() {
    }

    @Nullable
    Map<String, ArchiveEntry> calculateEntries() {
        if (this.entries == null) {
            try {
                SevenZFile file = new SevenZFile(_file, (char[]) null)
                file.withCloseable {
                    return this.entries = ArchiveUtils.calculateEntries(it)
                }
            } catch (IOException ignored) {
                return null
            }
        }
        else {
            return this.entries
        }
    }

}