package script

import com.github.camork.filesystem.gz.GZFile
import com.github.camork.filesystem.tar.TarFile
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.utils.IOUtils

import java.nio.charset.StandardCharsets

/**
 * @author Charles Wu
 */

File _file = new File("C:\\Users\\liferay\\IdeaProjects\\testarchive\\src\\targz\\apache-tomcat-8.5.9.tar.gz")
File dest = new File('D:\\git\\file-expander-plugin\\src\\test')

//uncompressTarGZ(_file, dest)

GZFile a = new GZFile(_file)

a.createEntriesInfoMap()
def bytes = a.getEntryBytes('apache-tomcat-8.5.9/webapps/examples/jsp/checkbox/checkresult.jsp')
bytes

getEntryBytes(getInputStream(_file), null)

static byte[] getEntryBytes(ArchiveInputStream inputStream, ArchiveEntry item) {
	ArchiveEntry entry

	while ((entry = inputStream.getNextEntry()) != null) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
		IOUtils.copy(inputStream, outputStream)
		def array = outputStream.toByteArray()

		println(new String(array, StandardCharsets.UTF_8))
	}

	return new byte[0]
}

static ArchiveInputStream getInputStream(File tarFile) {
	GzipCompressorInputStream gcis = new GzipCompressorInputStream(new FileInputStream(tarFile))
	return new TarArchiveInputStream(gcis)
}

static void uncompressTarGZ(File tarFile, File dest) throws IOException {
	dest.mkdirs()

	BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(tarFile))
	GzipCompressorInputStream gcis = new GzipCompressorInputStream(inputStream)
	new TarArchiveInputStream(gcis).withStream {
		tais ->
			ArchiveEntry entry
			while ((entry = tais.getNextEntry()) != null) {
				File desFile = new File(dest, entry.getName())
				if (entry.isDirectory()) {
					boolean mkDirs = desFile.mkdirs()
					if (!mkDirs) {
						println("Unable to create directory ${desFile.getAbsolutePath()}")
					}
				}
				else {
					boolean createNewFile = desFile.createNewFile();
					if (!createNewFile) {
						println("Unable to create file ${desFile.getAbsolutePath()}")
						continue
					}
					new BufferedOutputStream(new FileOutputStream(desFile)).withStream {

						IOUtils.copy(tais, it)
					}
				}
			}
			println("Untar completed successfully!")
	}
}
