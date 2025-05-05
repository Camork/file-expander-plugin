package com.github.camork.filesystem.gz

import com.github.camork.filesystem.IArchiveFile
import com.github.camork.util.EntryInfo
import org.assertj.core.api.WithAssertions
import java.io.File
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

abstract class ArchiveFileTest<T : IArchiveFile> : WithAssertions {

    protected abstract fun createArchiveFile(fileName: String): T

    protected fun getTestFile(fileName: String): File {
        val resource = javaClass.classLoader.getResource(fileName)
        requireNotNull(resource) { "Test file $fileName not found in resources" }
        return File(resource.file)
    }

    protected fun createEntriesInfoMap(file: IArchiveFile): Map<String, EntryInfo> {
        val entriesMap = file.createEntriesInfoMap()
        assertTrue(entriesMap.isNotEmpty(), "Entries map should not be empty")
        assertTrue(entriesMap.size >= 2, "Entries map should have at least 2 entries (root and content)")
        return entriesMap
    }

    protected fun assertInnerFileContent(archiveFile: T, innerFileName: String, func: (String) -> Unit) {
        assertNotNull(innerFileName, "Inner file name should not be null")
        val bytes: ByteArray = archiveFile.getEntryBytes(innerFileName)!!
        val content = String(bytes)
        func.invoke(content)
    }

}
