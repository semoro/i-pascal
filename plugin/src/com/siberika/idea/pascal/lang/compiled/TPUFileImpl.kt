package com.siberika.idea.pascal.lang.compiled

import com.intellij.openapi.fileTypes.BinaryFileTypeDecompilers
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiManager
import com.siberika.idea.pascal.TPUFileType

class TPUFileImpl(manager: PsiManager, fileViewProvider: FileViewProvider) : CompiledFileImpl(manager, fileViewProvider) {
    override fun decompile(manager: PsiManager?, file: VirtualFile?): String {
        val decompiler = BinaryFileTypeDecompilers.INSTANCE.forFileType(fileType)
        return decompiler.decompile(file).toString()
    }

    override fun getFileType(): FileType = TPUFileType
}