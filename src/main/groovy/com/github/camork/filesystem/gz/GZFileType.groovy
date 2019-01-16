package com.github.camork.filesystem.gz

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.NotNull

import javax.swing.Icon

/**
 * @author Charles Wu
 */
class GZFileType implements FileType {

	public static final FileType INSTANCE = new GZFileType()

	@Override
	String getName() {
		return "GZIP"
	}

	@Override
	String getDescription() {
		return null
	}

	@Override
	String getDefaultExtension() {
		return "gz"
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