package com.github.camork.filesystem.tar

import com.github.camork.filesystem.IArchiveFile
import com.github.camork.util.ArchiveUtils
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream

/**
 * @author Charles Wu
 */
class TarFile implements IArchiveFile {

    private final File _file

    private FileInputStream _fileInputStream

    private Map<String, ArchiveEntry> entries

    TarFile(File file) {
        this._file = file
        assert file.name.endsWith('.tar')
    }

    @Override
    ArchiveInputStream getInputStream() {
        _fileInputStream = new FileInputStream(_file)

        return new TarArchiveInputStream(_fileInputStream)
    }

    byte[] getEntryBytes(String relativePath) {
        ArchiveEntry item = calculateEntries().get(relativePath)

        return ArchiveUtils.getEntryBytes(inputStream, item)
    }

    Map<String, ArchiveEntry> calculateEntries() {
        if (entries == null) {
            entries = inputStream.withCloseable {
                ArchiveUtils.calculateEntries(it)
            }
        }
        else {
            entries
        }
    }

    Map<String, ?> createEntriesInfoMap() {
        return inputStream.withCloseable {
            ArchiveUtils.buildEntryMap(calculateEntries(), it)
        }
    }

    @Override
    void closeStream() {
        _fileInputStream?.close()
    }

}