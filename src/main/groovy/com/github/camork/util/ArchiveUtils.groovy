package com.github.camork.util

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.impl.ArchiveHandler
import com.intellij.util.text.ByteArrayCharSequence
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.utils.IOUtils
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

/**
 * @author Charles Wu
 */
class ArchiveUtils {

	public static final long DEFAULT_LENGTH = 0L
	public static final long DEFAULT_TIMESTAMP = -1L

	static byte[] getEntryBytes(ArchiveInputStream inputStream, ArchiveEntry item) {
		ArchiveEntry entry

		while ((entry = inputStream.getNextEntry()) != null) {
			if (item.name != entry.name) {
				continue
			}

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
			IOUtils.copy(inputStream, outputStream)

			return outputStream.toByteArray()
		}

		return new byte[0]
	}

	/**
	 * @param entries this archive's entries name map
	 * @param input a ArchiveInputStream
	 * @return constructed a archive entries map
	 */
	@NotNull
	static Map<String, ArchiveHandler.EntryInfo> buildEntryMap(Map<String, ArchiveEntry> entries, ArchiveInputStream input) {
		Map<String, ArchiveHandler.EntryInfo> map = new HashMap<>(entries.size())
		map.put("", createRootEntry())

		for (Map.Entry<String, ArchiveEntry> entry : entries.entrySet()) {
			getOrCreate(entry.value, map, entries)
		}

		return map
	}

	/**
	 * wrap the given {@link ArchiveEntry} to a {@link ArchiveHandler.EntryInfo}
	 *
	 * @param map map result
	 * @param entries all entries in this archive
	 * @return
	 */
	static ArchiveHandler.EntryInfo getOrCreate(@NotNull ArchiveEntry entry,
												@NotNull Map<String, ArchiveHandler.EntryInfo> map,
												@NotNull Map<String, ArchiveEntry> entries) {
		boolean isDirectory = entry.isDirectory()
		String entryName = entry.getName()
		if (StringUtil.endsWithChar(entryName, '/' as char)) {
			entryName = entryName.substring(0, entryName.length() - 1)
			isDirectory = true
		}

		//get from cache
		ArchiveHandler.EntryInfo info = map.get(entryName)
		if (info != null) {
			return info
		}

		//create node
		Pair<String, String> path = splitPath(entryName)
		ArchiveHandler.EntryInfo parentInfo = getOrCreate(path.first, map, entries)
		if ('.' == path.second) {
			return parentInfo
		}
		info = store(map, parentInfo, path.second, isDirectory, entry.getSize(), entry.lastModifiedDate.time, entryName);
		return info
	}

	@NotNull
	static ArchiveHandler.EntryInfo getOrCreate(@NotNull String entryName,
												@NotNull Map<String, ArchiveHandler.EntryInfo> map,
												@NotNull Map<String, ArchiveEntry> entries) {
		ArchiveHandler.EntryInfo info = map.get(entryName)

		if (info == null) {
			ArchiveEntry entry = entries.get(entryName + '/')
			if (entry != null) {
				return getOrCreate(entry, map, entries)
			}

			Pair<String, String> path = splitPath(entryName)

			ArchiveHandler.EntryInfo parentInfo = getOrCreate(path.first, map, entries)
			info = store(map, parentInfo, path.second, true, DEFAULT_LENGTH, DEFAULT_TIMESTAMP, entryName)
		}

		if (!info.isDirectory) {
			Logger.getInstance(this.class).info(entryName + " should be a directory")
			info = store(map, info.parent, info.shortName, true, info.length, info.timestamp, entryName)
		}

		return info
	}

	@NotNull
	static Map<String, ArchiveEntry> calculateEntries(ArchiveInputStream input) {
		Map<String, ArchiveEntry> map = new LinkedHashMap<>()

		ArchiveEntry entry

		while ((entry = input.getNextEntry()) != null) {
			map.put(entry.getName(), entry)
		}

		return map
	}

	@NotNull
	private static ArchiveHandler.EntryInfo store(@NotNull Map<String, ArchiveHandler.EntryInfo> map,
												  @Nullable ArchiveHandler.EntryInfo parentInfo,
												  @NotNull CharSequence shortName,
												  boolean isDirectory,
												  long size,
												  long time,
												  @NotNull String entryName) {
		CharSequence sequence = shortName instanceof ByteArrayCharSequence ? shortName : ByteArrayCharSequence.convertToBytesIfPossible(shortName)
		ArchiveHandler.EntryInfo info = new ArchiveHandler.EntryInfo(sequence, isDirectory, size, time, parentInfo)
		map.put(entryName, info)
		return info
	}

	@NotNull
	private static Pair<String, String> splitPath(@NotNull String entryName) {
		int p = entryName.lastIndexOf('/')
		String parentName = p > 0 ? entryName.substring(0, p) : ""
		String shortName = p > 0 ? entryName.substring(p + 1) : entryName
		return Pair.create(parentName, shortName)
	}

	@NotNull
	private static ArchiveHandler.EntryInfo createRootEntry() {
		return new ArchiveHandler.EntryInfo("", true, DEFAULT_LENGTH, DEFAULT_TIMESTAMP, null)
	}

}