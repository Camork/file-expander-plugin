package com.github.camork.filesystem

import com.github.camork.util.EntryInfo

/**
 * @author Charles Wu
 */
interface IArchiveFile extends Closeable{

    Map<String, EntryInfo> createEntriesInfoMap()

    byte[] getEntryBytes(String relativePath)

    InputStream getInputStream()

}