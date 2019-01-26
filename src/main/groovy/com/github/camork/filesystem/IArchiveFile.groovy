package com.github.camork.filesystem

/**
 * @author Charles Wu
 */
interface IArchiveFile {

    InputStream getInputStream()

    void closeStream()

}