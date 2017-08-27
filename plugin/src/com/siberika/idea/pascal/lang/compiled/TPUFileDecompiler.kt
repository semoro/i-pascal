package com.siberika.idea.pascal.lang.compiled

import com.intellij.openapi.fileTypes.BinaryFileDecompiler
import com.intellij.openapi.vfs.VirtualFile

class TPUFileDecompiler : BinaryFileDecompiler {
    override fun decompile(file: VirtualFile): CharSequence {

        val builder = StringBuilder()
        builder.apply {
            appendln("Unit ${file.nameWithoutExtension};")
            appendln("interface")
            appendln("procedure P;")
        }

        return builder
    }

}