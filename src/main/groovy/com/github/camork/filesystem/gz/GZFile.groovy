package com.github.camork.filesystem.gz

import com.github.camork.filesystem.IArchiveFile
import com.github.camork.filesystem.tar.TarGzFile
import com.github.camork.util.ArchiveUtils
import com.github.camork.util.EntryInfo
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.gzip.GzipUtils

/**
 * @author Charles Wu
 */
class GZFile implements IArchiveFile {

    private final File _file

    private String _name

    private BufferedInputStream _inputStream

    private GzipCompressorInputStream _gZIPInputStream

    private TarGzFile _tarGzFile

    /**
     * If it's a tar.gz file, delegate to {@link TarGzFile} to handle all
     */
    GZFile(File file) {
        _file = file

        if (_file.name.endsWith(".tar.gz")) {
            _tarGzFile = new TarGzFile(_file)
        }
        else {
            _name = GzipUtils.getUncompressedFilename(file.getName())
        }
    }

    @Override
    Map<String, ?> createEntriesInfoMap() {
        if (_tarGzFile != null) {
            return _tarGzFile.createEntriesInfoMap()
        }

        Map entries = [:]

        EntryInfo root = ArchiveUtils.createRootEntry()

        entries.put('', root)
        entries.put(_name, new EntryInfo(_name, false, _file.length(), _file.lastModified(), root))

        return entries
    }

    @Override
    byte[] getEntryBytes(String relativePath) {
        if (_tarGzFile != null) {
            return _tarGzFile.getEntryBytes(relativePath)
        }

        return inputStream.bytes
    }

    @Override
    InputStream getInputStream() {
        if (_tarGzFile != null) {
            return _tarGzFile.inputStream
        }

        _inputStream = _file.newInputStream()

        return _gZIPInputStream = new GzipCompressorInputStream(_inputStream)
    }

    @Override
    void close() {
        _gZIPInputStream?.close()
        _inputStream?.close()
    }

}