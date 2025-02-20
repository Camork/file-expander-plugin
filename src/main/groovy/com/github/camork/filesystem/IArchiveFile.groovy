package com.github.camork.filesystem

import com.github.camork.util.EntryInfo
import org.jetbrains.annotations.Nullable

/**
 * The basic interface of a archive file,
 * all customized archive types should implement it.
 *
 * @author Charles Wu
 */
interface IArchiveFile extends Closeable {

    Map<String, EntryInfo> createEntriesInfoMap()

    byte[] getEntryBytes(String relativePath)

    @Nullable
    InputStream getInputStream()

}