package com.siberika.idea.pascal.lang.compiled;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.ContentBasedClassFileProcessor;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.siberika.idea.pascal.PPUFileType;
import com.siberika.idea.pascal.PascalLanguage;
import com.siberika.idea.pascal.util.ModuleUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Author: George Bakhtadze
 * Date: 13/11/2013
 */
public class PPUContentBasedClassFileProcessor implements ContentBasedClassFileProcessor {
    @NotNull
    @Override
    public SyntaxHighlighter createHighlighter(Project project, VirtualFile vFile) {
        return SyntaxHighlighterFactory.getSyntaxHighlighter(PascalLanguage.INSTANCE, project, vFile);
    }

    @Override
    public boolean isApplicable(Project project, VirtualFile vFile) {
        return vFile.getFileType() == PPUFileType.INSTANCE;
    }

    @NotNull
    @Override
    public String obtainFileText(Project project, VirtualFile file) {
        return PPUFileDecompiler.decompileText(file.getPath(), ModuleUtil.getModuleForFile(project, file));
    }

    @Nullable
    @Override
    public Language obtainLanguageForFile(VirtualFile file) {
        return null;
    }
}
