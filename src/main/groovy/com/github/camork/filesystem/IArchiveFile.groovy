package com.github.camork.filesystem

import com.github.camork.util.EntryInfo

/**
 * Base interface of a archive file if you want to implement
 *
 * @author Charles Wu
 */
interface IArchiveFile extends Closeable{

    Map<String, EntryInfo> createEntriesInfoMap()

    byte[] getEntryBytes(String relativePath)

    InputStream getInputStream()

}