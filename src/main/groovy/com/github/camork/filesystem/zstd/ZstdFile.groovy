package com.github.camork.filesystem.zstd

import com.github.camork.filesystem.IArchiveFile
import com.github.camork.util.ArchiveUtils
import com.github.camork.util.EntryInfo
import com.github.luben.zstd.ZstdInputStream

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
        entries.put('contents', new EntryInfo('contents', false, file.length(), file.lastModified(), root))

        return entries
    }

    @Override
    byte[] getEntryBytes(String relativePath) {
        return getInputStream().withCloseable { it.bytes }
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