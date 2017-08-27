package com.siberika.idea.pascal

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

object TPUFileType : FileType {
    override fun getDefaultExtension(): String = "TPU"

    override fun getIcon(): Icon? = PascalIcons.UNIT

    override fun getCharset(file: VirtualFile, content: ByteArray): String? = null

    override fun getName(): String = "TP_Unit"

    override fun getDescription(): String = "Turbo Pascal Unit"

    override fun isBinary(): Boolean {
        return true
    }

    override fun isReadOnly(): Boolean {
        return true
    }

}