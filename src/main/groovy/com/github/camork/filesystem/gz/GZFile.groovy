package com.github.camork.filesystem.gz

import com.github.camork.filesystem.IArchiveFile
import com.github.camork.filesystem.tar.TarGzFile
import com.github.camork.filesystem.tar.TarGzFileType
import com.github.camork.util.ArchiveUtils
import com.github.camork.util.CoreUtil
import com.github.camork.util.EntryInfo
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

/**
 * @author Charles Wu
 */
class GZFile implements IArchiveFile {

    private final File _file

    private String _innerName

    private BufferedInputStream _inputStream

    private GzipCompressorInputStream _gZIPInputStream

    private TarGzFile _tarGzFile

    /**
     * If it's a tar with gz based file, delegate to {@link TarGzFile} to handle all
     */
    GZFile(File file) {
        _file = file

        String fileName = _file.name
        if (fileName.endsWith(".tar.gz") || fileName.endsWith(CoreUtil.DOT + TarGzFileType.INSTANCE.defaultExtension)) {
            _tarGzFile = new TarGzFile(_file)
        }
    }

    @Override
    Map<String, ?> createEntriesInfoMap() {
        if (_tarGzFile != null) {
            return _tarGzFile.createEntriesInfoMap()
        }

        (inputStream as GzipCompressorInputStream).withCloseable {
            _innerName = it.metaData.filename
        }
        Map entries = [:]

        EntryInfo root = ArchiveUtils.createRootEntry()

        entries.put('', root)
        entries.put(_innerName, new EntryInfo(_innerName, false, _file.length(), _file.lastModified(), root))

        return entries
    }

    @Override
    byte[] getEntryBytes(String relativePath) {
        if (_tarGzFile != null) {
            return _tarGzFile.getEntryBytes(relativePath)
        }

        return inputStream.withCloseable {
            it.bytes
        }
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