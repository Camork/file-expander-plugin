package com.github.camork.filesystem.gz

import org.junit.jupiter.api.Test

class GZFileTest : ArchiveFileTest<GZFile>() {

    override fun createArchiveFile(fileName: String): GZFile {
        return GZFile(getTestFile("gz/$fileName"))
    }

    @Test
    fun testArchiveFileOpensCorrectly() {
        val file = createArchiveFile("concatenate_test.tsv.gz")

        val entriesMap = createEntriesInfoMap(file)
        val innerFileName = entriesMap.keys.first { it.isNotEmpty() }

        assertInnerFileContent(file, innerFileName) { str ->
            assertThat(str).hasLineCount(188820)
        }
    }

}