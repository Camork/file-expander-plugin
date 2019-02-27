package script

import com.google.common.io.Files

/**
 * @author Charles Wu
 */

static File createTempDirectory(String dirName) {
    File baseDir = new File(System.getProperty("java.io.tmpdir"))
    File tempDir = new File(baseDir, dirName)
    if (tempDir.mkdir()) {
        return tempDir
    }

    if (tempDir.exists()) {
        if (tempDir.canWrite()) {
            return tempDir
        }
        else {
            throw new IOException("Folder: ${tempDir.getAbsolutePath()} cannot be writed!")
        }
    }

    return null
}

def directory = createTempDirectory("fileExpanderFiles")

print(directory)