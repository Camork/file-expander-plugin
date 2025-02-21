package com.github.camork.filesystem

import com.github.camork.util.EntryInfo
import java.io.Closeable
import java.io.InputStream

/**
 * The basic interface of an archive file,
 * all customized archive types should implement it.
 *
 * @author Charles Wu
 */
interface IArchiveFile : Closeable {

    fun createEntriesInfoMap(): Map<String, EntryInfo>

    fun getEntryBytes(relativePath: String): ByteArray?

    fun getInputStream(): InputStream?
}