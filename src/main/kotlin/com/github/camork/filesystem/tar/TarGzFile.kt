package com.github.camork.filesystem.tar

import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

/**
 * @author Charles Wu
 */
class TarGzFile(file: File) : TarFile(file) {

    private var _gzInputStream: GzipCompressorInputStream? = null

    override fun getInputStream(): ArchiveInputStream<TarArchiveEntry>? {
        _fileInputStream = FileInputStream(_file)

        return try {
            _gzInputStream = GzipCompressorInputStream(_fileInputStream)
            TarArchiveInputStream(_gzInputStream)
        } catch (_: IOException) {
            _fileInputStream?.close()
            null
        }
    }

    override fun close() {
        super.close()
        _gzInputStream?.close()
    }
}