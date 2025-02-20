package com.github.camork.filesystem.tar

import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

/**
 * @author Charles Wu
 */
class TarGzFile extends TarFile {

    protected GzipCompressorInputStream _gcInputStream

    TarGzFile(File file) {
        super(file)
    }

    @Override
    ArchiveInputStream getInputStream() {
        _fileInputStream = new FileInputStream(_file)

        try {
            _gcInputStream = new GzipCompressorInputStream(_fileInputStream)
        } catch (IOException ignored) {
            _fileInputStream.close()
            return null
        }
        return new TarArchiveInputStream(_gcInputStream)
    }

    @Override
    void close() {
        super.close()

        _gcInputStream.close()
    }

}
