package com.siberika.idea.pascal.debugger.gdb;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XStackFrame;
import com.siberika.idea.pascal.debugger.gdb.parser.GdbMiResults;
import com.siberika.idea.pascal.util.StrUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Author: George Bakhtadze
 * Date: 01/04/2017
 */
public class GdbStackFrame extends XStackFrame {
    private final PascalXDebugProcess process;
    private final GdbExecutionStack executionStack;
    private final GdbMiResults frame;
    private final int level;

    public GdbStackFrame(GdbExecutionStack executionStack, GdbMiResults frame) {
        this.process = executionStack.getProcess();
        this.executionStack = executionStack;
        this.frame = frame;
        level = (frame != null) && (frame.getValue("level") != null) ? StrUtil.strToIntDef(frame.getString("level"), 0) : 0;
    }

    public XSourcePosition getSourcePosition() {
        if (null == frame) {
            return null;
        }
        String line = frame.getString("line");
        String filename = frame.getString("fullname");
        if ((null == filename) || (null == line)) {
            return null;
        }

        String path = filename.replace(File.separatorChar, '/');
        VirtualFile file = LocalFileSystem.getInstance().findFileByPath(path);
        if (null == file) {
            return null;
        }

        return XDebuggerUtil.getInstance().createPosition(file, Integer.parseInt(line) - 1); //TODO: strToIntDef()
    }

    @Override
    public void customizePresentation(@NotNull ColoredTextContainer component) {
        if (null == frame) {
            return;
        }
        String filename = frame.getString("file");
        String line = frame.getString("line");
        component.append(formatRoutine(frame), SimpleTextAttributes.REGULAR_ATTRIBUTES);
        component.append(String.format(" (%s:%s)", filename != null ? filename : "-", line != null ? line : "-"), SimpleTextAttributes.GRAYED_ATTRIBUTES);
        component.setIcon(AllIcons.Debugger.StackFrame);
    }

    private String formatRoutine(GdbMiResults frame) {
        String routine = frame.getString("func");
        if (StringUtils.isEmpty(routine) || "??".equals(routine)) {
            String addr = frame.getString("addr");
            routine = String.format("?? (%s)", addr != null ? addr : "-");
        }
        return routine + "()";
    }

    @Nullable
    @Override
    public XDebuggerEvaluator getEvaluator() {
        return new GdbEvaluator(this);
    }

    @Override
    public void computeChildren(@NotNull XCompositeNode node) {
        process.setLastQueriedVariablesCompositeNode(node);
        process.sendCommand(String.format("-stack-list-variables --thread %s --frame %d --simple-values", executionStack.getThreadId(), level));
    }

    public GdbExecutionStack getExecutionStack() {
        return executionStack;
    }

    public int getLevel() {
        return level;
    }
}