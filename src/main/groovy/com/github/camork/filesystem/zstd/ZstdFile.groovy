package com.github.camork.filesystem.zstd

import com.github.camork.filesystem.IArchiveFile
import com.github.camork.util.ArchiveUtils
import com.github.camork.util.EntryInfo
import io.airlift.compress.zstd.ZstdInputStream

class ZstdFile implements IArchiveFile {
    private final File file
    private ZstdInputStream zstdInputStream

    ZstdFile(File file) {
        this.file = file
    }

    @Override
    Map<String, ?> createEntriesInfoMap() {
        Map entries = new HashMap<String, ?>()
        EntryInfo root = ArchiveUtils.createRootEntry()
        entries.put('', root)

        def entryName = file.name.replace('.zst', '')
        entries.put(entryName, new EntryInfo(entryName, false, file.length(), file.lastModified(), root))

        return entries
    }

    @Override
    byte[] getEntryBytes(String relativePath) {
        return getInputStream()?.withCloseable { it.bytes }
    }

    @Override
    InputStream getInputStream() {
        return zstdInputStream = new ZstdInputStream(file.newInputStream())
    }

    @Override
    void close() {
        zstdInputStream?.close()
    }
}
