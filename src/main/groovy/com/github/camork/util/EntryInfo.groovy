package com.github.camork.util

import com.intellij.openapi.vfs.impl.ArchiveHandler
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

/**
 * @author Charles Wu
 *
 * Place-holder class to access'EntryInfo'
 */
class EntryInfo extends ArchiveHandler.EntryInfo {
    EntryInfo(@NotNull CharSequence shortName, boolean isDirectory,
              long length, long timestamp, @Nullable ArchiveHandler.EntryInfo parent) {
        super(shortName, isDirectory, length, timestamp, parent)
    }
}