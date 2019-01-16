package com.github.camork.filesystem.gz

import org.apache.commons.compress.compressors.gzip.GzipUtils

import java.util.zip.GZIPInputStream

/**
 * @author Charles Wu
 */
class GZFile {

	private final File _file

	private final long length

	private final long lastModified

	private final String name

	GZFile(File file) {
		_file = file
		length = _file.length()
		lastModified = _file.lastModified()

		name = GzipUtils.getUncompressedFilename(file.getName())
	}

	long getLength() {
		length
	}

	String getName() {
		name
	}

	long getLastModified() {
		lastModified
	}

	byte[] getBytes() {
		_file.withInputStream {
			stream ->
				new GZIPInputStream(stream).withCloseable {
					gzStream -> gzStream.getBytes()
				}
		} as byte[]
	}

}