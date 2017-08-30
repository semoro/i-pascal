package com.siberika.idea.pascal.lang.compiled

import com.intellij.openapi.fileEditor.impl.LoadTextUtil
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiManager
import com.siberika.idea.pascal.TPUFileType


class TPUDFileImpl(manager: PsiManager, fileViewProvider: FileViewProvider) : CompiledFileImpl(manager, fileViewProvider) {
    override fun decompile(manager: PsiManager?, file: VirtualFile?): String {
        return  LoadTextUtil.loadText(file!!).toString()
    }

    override fun getFileType(): FileType = TPUFileType
}