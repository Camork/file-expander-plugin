package com.github.camork.filesystem.tar

import com.github.camork.filesystem.IArchiveFile
import com.github.camork.util.ArchiveUtils
import com.github.camork.util.EntryInfo
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream

/**
 * @author Charles Wu
 */
class TarFile implements IArchiveFile {

    protected final File _file

    protected FileInputStream _fileInputStream

    protected Map<String, ArchiveEntry> entries

    TarFile(File file) {
        _file = file
    }

    @Override
    Map<String, EntryInfo> createEntriesInfoMap() {
        return inputStream.withCloseable {
            ArchiveUtils.buildEntryMap(calculateEntries())
        }
    }

    @Override
    byte[] getEntryBytes(String relativePath) {
        ArchiveEntry item = calculateEntries().get(relativePath)

        return ArchiveUtils.getEntryBytes(inputStream, item)
    }

    @Override
    ArchiveInputStream getInputStream() {
        _fileInputStream = new FileInputStream(_file)

        return new TarArchiveInputStream(_fileInputStream)
    }

    @Override
    void close() {
        _fileInputStream?.close()
    }

    Map<String, ArchiveEntry> calculateEntries() {
        if (entries == null) {
            return entries = inputStream.withCloseable {
                ArchiveUtils.calculateEntries(it)
            }
        }
        else {
            return entries
        }
    }

}