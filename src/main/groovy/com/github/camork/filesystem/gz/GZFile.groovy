package com.github.camork.filesystem.gz

import com.github.camork.filesystem.IArchiveFile
import com.github.camork.filesystem.tar.TarGzFile
import com.github.camork.filesystem.tar.TarGzFileType
import com.github.camork.util.ArchiveUtils
import com.github.camork.util.CoreUtil
import com.github.camork.util.EntryInfo
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.jetbrains.annotations.Nullable

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

        Map<String, ?> entries = [:]

        InputStream stream = getInputStream()
        if (stream == null) {
            return entries
        }

        (stream as GzipCompressorInputStream).withCloseable {
            _innerName = it.metaData.filename
        }

        if (_innerName == null) {
            int idx = _file.name.indexOf(CoreUtil.DOT + GZFileType.INSTANCE.defaultExtension)
            _innerName = idx == -1 ? "contents" : _file.name.substring(0, idx)
        }

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

        InputStream stream = getInputStream()
        if (stream == null) {
            return new byte[0]
        }

        return stream?.withCloseable {
            it.bytes
        }
    }

    @Nullable
    @Override
    InputStream getInputStream() {
        if (_tarGzFile != null) {
            return _tarGzFile.inputStream
        }

        _inputStream = _file.newInputStream()

        try {
            return _gZIPInputStream = new GzipCompressorInputStream(_inputStream)
        } catch (IOException ignored) {
            _inputStream.close()
            return null
        }
    }

    @Override
    void close() {
        _gZIPInputStream?.close()
        _inputStream?.close()
    }

}