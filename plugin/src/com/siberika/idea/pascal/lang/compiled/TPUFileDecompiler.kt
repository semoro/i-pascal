package com.siberika.idea.pascal.lang.compiled

import com.intellij.openapi.fileTypes.BinaryFileDecompiler
import com.intellij.openapi.vfs.VirtualFile
import me.semoro.pascal.tpu.decompiler.DecompilationSession

class TPUFileDecompiler : BinaryFileDecompiler {
    override fun decompile(file: VirtualFile): CharSequence {

        val data = file.contentsToByteArray()

        val decompilerSession = DecompilationSession(data)
        decompilerSession.decompile()
        return decompilerSession.sb
    }

}