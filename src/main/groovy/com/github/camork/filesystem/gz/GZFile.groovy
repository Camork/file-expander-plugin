package com.github.camork.filesystem.gz

import com.github.camork.filesystem.IArchiveFile
import com.github.camork.util.ArchiveUtils
import com.github.camork.util.EntryInfo
import org.apache.commons.compress.compressors.gzip.GzipUtils

import java.util.zip.GZIPInputStream

/**
 * @author Charles Wu
 */
class GZFile implements IArchiveFile {

    private final File _file

    private final String _name

    private BufferedInputStream _inputStream

    private GZIPInputStream _gZIPInputStream

    GZFile(File file) {
        _file = file

        _name = GzipUtils.getUncompressedFilename(file.getName())
    }

    @Override
    Map<String, ?> createEntriesInfoMap() {
        Map entries = [:]

        EntryInfo root = new EntryInfo('', true, ArchiveUtils.DEFAULT_LENGTH, ArchiveUtils.DEFAULT_TIMESTAMP, null)
        entries.put('', root)
        entries.put(_name, new EntryInfo(_name, false, _file.length(), _file.lastModified(), root))

        return entries
    }

    @Override
    byte[] getEntryBytes(String relativePath) {
        return inputStream.bytes
    }

    @Override
    InputStream getInputStream() {
        _inputStream = _file.newInputStream()

        return _gZIPInputStream = new GZIPInputStream(_inputStream)
    }

    @Override
    void closeStream() {
        _gZIPInputStream.close()
        _inputStream.close()
    }

}