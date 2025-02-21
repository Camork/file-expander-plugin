package com.github.camork.filesystem.gz

import com.github.camork.filesystem.IArchiveFile
import com.github.camork.filesystem.tar.TarGzFile
import com.github.camork.filesystem.tar.TarGzFileType
import com.github.camork.util.ArchiveUtils
import com.github.camork.util.CoreUtil
import com.github.camork.util.EntryInfo
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * @author Charles Wu
 */
class GZFile(private val _file: File) : IArchiveFile {

    private var _innerName: String? = null
    private var _inputStream: BufferedInputStream? = null
    private var _gZIPInputStream: GzipCompressorInputStream? = null
    private var _tarGzFile: TarGzFile? = null

    /**
     * If it's a tar with gz based file, delegate to {@link TarGzFile} to handle all
     */
    init {
        val fileName = _file.name
        if (fileName.endsWith(".tar.gz") || fileName.endsWith(CoreUtil.DOT + TarGzFileType.defaultExtension)) {
            _tarGzFile = TarGzFile(_file)
        }
    }

    override fun createEntriesInfoMap(): Map<String, EntryInfo> {
        _tarGzFile?.let {
            return it.createEntriesInfoMap()
        }

        val entries = mutableMapOf<String, EntryInfo>()
        val stream = getInputStream() ?: return entries

        (stream as GzipCompressorInputStream).use {
            _innerName = it.metaData.filename
        }

        if (_innerName == null) {
            val idx = _file.name.indexOf(CoreUtil.DOT + GZFileType.defaultExtension)
            _innerName = if (idx == -1) "contents" else _file.name.substring(0, idx)
        }

        val root = ArchiveUtils.createRootEntry()
        entries[""] = root
        entries[_innerName!!] = EntryInfo(_innerName!!, false, _file.length(), _file.lastModified(), root)

        return entries
    }

    override fun getEntryBytes(relativePath: String): ByteArray? {
        _tarGzFile?.let {
            return it.getEntryBytes(relativePath)
        }

        val stream = getInputStream() ?: return ByteArray(0)
        return stream.use { it.readBytes() }
    }

    override fun getInputStream(): InputStream? {
        _tarGzFile?.let {
            return it.getInputStream()
        }

        _inputStream = _file.inputStream().buffered()
        return try {
            _gZIPInputStream = GzipCompressorInputStream(_inputStream)
            _gZIPInputStream
        } catch (_: IOException) {
            _inputStream?.close()
            null
        }
    }

    override fun close() {
        _gZIPInputStream?.close()
        _inputStream?.close()
    }
}