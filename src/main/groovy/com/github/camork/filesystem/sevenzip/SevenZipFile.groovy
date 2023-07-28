package com.github.camork.filesystem.sevenzip

import com.github.camork.filesystem.IArchiveFile
import com.github.camork.util.ArchiveUtils
import com.github.camork.util.EntryInfo
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.sevenz.SevenZFile

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
        return ArchiveUtils.buildEntryMap(calculateEntries())
    }

    @Override
    byte[] getEntryBytes(String relativePath) {
        ArchiveEntry item = calculateEntries().get(relativePath)

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

    Map<String, ArchiveEntry> calculateEntries() {
        if (entries == null) {
            new SevenZFile(_file, (char[]) null).withCloseable {
                return entries = ArchiveUtils.calculateEntries(it)
            }
        }
        else {
            return entries
        }
    }

}