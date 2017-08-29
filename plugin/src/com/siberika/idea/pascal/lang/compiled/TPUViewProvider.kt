package com.siberika.idea.pascal.lang.compiled

import com.intellij.ide.highlighter.JavaClassFileType
import com.intellij.lang.Language
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.FileIndexFacade
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.siberika.idea.pascal.PascalLanguage
import com.siberika.idea.pascal.TPUFileType

class TPUViewProvider(
        manager: PsiManager,
        virtualFile: VirtualFile,
        physical: Boolean = true
) : ClassFileViewProvider(manager, virtualFile, physical), FileViewProvider {

    override fun createFile(project: Project, vFile: VirtualFile, fileType: FileType): PsiFile? {
        val fileIndex = ServiceManager.getService(project, FileIndexFacade::class.java)
        val fType = if (fileType is JavaClassFileType) vFile.fileType else fileType
        //f (fileIndex.isInLibraryClasses(vFile) || !fileIndex.isInSource(vFile)) {
            if (fType is TPUFileType) {
                return TPUFileImpl(manager, this)
            }
        //}
        return null
    }

    override fun getBaseLanguage(): Language = PascalLanguage.INSTANCE
}