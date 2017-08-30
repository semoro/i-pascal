package com.siberika.idea.pascal

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

object TPUDFileType : FileType {
    override fun getDefaultExtension(): String = "TPUD"

    override fun getIcon(): Icon? = PascalIcons.UNIT

    override fun getCharset(file: VirtualFile, content: ByteArray): String? = null

    override fun getName(): String = "TP_Unit_Decompiled"

    override fun getDescription(): String = "Decompiled Turbo Pascal Unit"

    override fun isBinary(): Boolean {
        return false
    }

    override fun isReadOnly(): Boolean {
        return true
    }
}