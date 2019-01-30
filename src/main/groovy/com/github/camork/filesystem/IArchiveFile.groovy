package com.github.camork.filesystem

import com.github.camork.util.EntryInfo

/**
 * @author Charles Wu
 */
interface IArchiveFile {

    Map<String, EntryInfo> createEntriesInfoMap()

    byte[] getEntryBytes(String relativePath)

    InputStream getInputStream()

    void closeStream()

}