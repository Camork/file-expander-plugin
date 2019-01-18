package com.github.camork.filesystem

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.NotNull

import javax.swing.Icon

/**
 * @author Charles Wu
 */
abstract class ArchiveBasedFileType implements FileType {

	@Override
	String getDescription() {
		return null
	}

	@Override
	Icon getIcon() {
		return AllIcons.FileTypes.Archive
	}

	@Override
	boolean isBinary() {
		return true
	}

	@Override
	boolean isReadOnly() {
		return false
	}

	@Override
	String getCharset(@NotNull VirtualFile file, @NotNull byte[] content) {
		return null
	}

}