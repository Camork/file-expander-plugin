package com.github.camork.util

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.text.StringUtil
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import org.apache.commons.io.IOUtils
import java.io.ByteArrayOutputStream

/**
 * @author Charles Wu
 */
object ArchiveUtils {

    const val DEFAULT_LENGTH = 0L
    const val DEFAULT_TIMESTAMP = -1L

    fun <T : ArchiveEntry> getEntryBytes(inputStream: ArchiveInputStream<T>, item: ArchiveEntry): ByteArray {
        var entry: ArchiveEntry?

        while (inputStream.nextEntry.also { entry = it } != null) {
            if (item.name != entry!!.name) {
                continue
            }

            val outputStream = ByteArrayOutputStream()
            IOUtils.copy(inputStream, outputStream)

            return outputStream.toByteArray()
        }

        return ByteArray(0)
    }

    fun getEntryBytes(file: SevenZFile, item: ArchiveEntry): ByteArray {
        var entry: ArchiveEntry?

        while (file.nextEntry.also { entry = it } != null) {
            if (item.name != entry!!.name) {
                continue
            }

            val bytes = ByteArray(entry.size.toInt())
            file.read(bytes)

            return bytes
        }

        return ByteArray(0)
    }

    /**
     * @param entries this archive's entries name map
     *
     * @return constructed a archive entries info map
     */
    fun buildEntryMap(entries: Map<String, ArchiveEntry>): Map<String, EntryInfo> {
        val map = HashMap<String, EntryInfo>(entries.size)
        map[""] = createRootEntry()

        for ((_, value) in entries) {
            getOrCreate(value, map, entries)
        }

        return map
    }

    /**
     * wrap the given {@link ArchiveEntry} to a {@link EntryInfo}
     *
     * @param map map result
     * @param entries all entries in this archive
     * @return
     */
    fun getOrCreate(
        entry: ArchiveEntry,
        map: MutableMap<String, EntryInfo>,
        entries: Map<String, ArchiveEntry>
    ): EntryInfo {
        var isDirectory = entry.isDirectory
        var entryName = entry.name
        if (StringUtil.endsWithChar(entryName, '/')) {
            entryName = entryName.substring(0, entryName.length - 1)
            isDirectory = true
        }

        //get from cache
        var info = map[entryName]
        if (info != null) {
            return info
        }

        val path = splitPath(entryName)
        val parentInfo = getOrCreate(path.first, map, entries)
        if ("." == path.second) {
            return parentInfo
        }
        info = store(map, parentInfo, path.second, isDirectory, entry.size, entry.lastModifiedDate.time, entryName)
        return info
    }

    fun getOrCreate(
        entryName: String,
        map: MutableMap<String, EntryInfo>,
        entries: Map<String, ArchiveEntry>
    ): EntryInfo {
        var info = map[entryName]

        if (info == null) {
            val entry = entries["$entryName/"]
            if (entry != null) {
                return getOrCreate(entry, map, entries)
            }

            val path = splitPath(entryName)
            val parentInfo = getOrCreate(path.first, map, entries)
            info = store(map, parentInfo, path.second, true, DEFAULT_LENGTH, DEFAULT_TIMESTAMP, entryName)
        }

        if (!info.isDirectory) {
            Logger.getInstance(this::class.java).info("$entryName should be a directory")
            info = store(map, info.parent, info.shortName.toString(), true, info.length, info.timestamp, entryName)
        }

        return info
    }

    fun <T : ArchiveEntry> calculateEntries(input: ArchiveInputStream<T>): Map<String, ArchiveEntry> {
        val map = LinkedHashMap<String, ArchiveEntry>()

        var entry: ArchiveEntry?
        while (input.nextEntry.also { entry = it } != null) {
            map[entry!!.name] = entry
        }

        return map
    }

    fun calculateEntries(file: SevenZFile): Map<String, ArchiveEntry> {
        val map = LinkedHashMap<String, ArchiveEntry>()

        file.entries.forEach { map[it.name] = it }

        return map
    }

    fun createRootEntry(): EntryInfo {
        return EntryInfo("", true, DEFAULT_LENGTH, DEFAULT_TIMESTAMP, null)
    }

    private fun store(
        map: MutableMap<String, EntryInfo>,
        parentInfo: EntryInfo?,
        shortName: String,
        isDirectory: Boolean,
        size: Long,
        time: Long,
        entryName: String
    ): EntryInfo {
        val info = EntryInfo(shortName, isDirectory, size, time, parentInfo)
        map[entryName] = info
        return info
    }

    private fun splitPath(entryName: String): Pair<String, String> {
        val p = entryName.lastIndexOf('/')
        val parentName = if (p > 0) entryName.substring(0, p) else ""
        val shortName = if (p > 0) entryName.substring(p + 1) else entryName
        return Pair.create(parentName, shortName)
    }
}