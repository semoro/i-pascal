package com.siberika.idea.pascal.editor.completion

import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.VirtualFile
import com.siberika.idea.pascal.PascalIcons
import com.siberika.idea.pascal.lang.psi.PasTypeDeclaration
import com.siberika.idea.pascal.lang.psi.impl.PasField
import com.siberika.idea.pascal.lang.psi.impl.PascalRoutineImpl
import com.siberika.idea.pascal.lang.references.PasReferenceUtil
import com.siberika.idea.pascal.util.DocUtil
import com.siberika.idea.pascal.util.PsiUtil

class PascalCompletionLookupElementFactory {

    private val PRIORITY_HIGHER = 10.0
    private val PRIORITY_LOWER = -10.0
    private val PRIORITY_LOWEST = -100.0


    private fun PasField.calculateTypeText(): String {

        PasReferenceUtil.retrieveFieldTypeScope(this)

        var typeText = ""
        val typeDeclaration = this.valueType?.declaration?.element?.parent as? PasTypeDeclaration
        if (typeDeclaration != null) {
            typeText = if (this.fieldType == PasField.FieldType.TYPE) {
                typeDeclaration.typeDecl?.text ?: ""
            } else {
                typeDeclaration.genericTypeIdent.text
            }
        }

        return typeText
    }

    fun PascalRoutineImpl.calculateParameterListPresentation(): String {
        val res = StringBuilder()
        val params = this.formalParameterSection
        if (params != null) {
            res.append("(")
            var nonFirst = false
            for (param in params.formalParameterList) {
                val td = param.typeDecl
                var typeStr = PsiUtil.TYPE_UNTYPED_NAME
                if (td != null) {
                    typeStr = td.text
                }
                for (i in 0 until param.namedIdentList.size) {
                    res.append(if (nonFirst) "," else "").append(typeStr)
                    nonFirst = true
                }
            }
            res.append(")")
        } else {
            res.append("()")
        }
        return res.toString()
    }

    fun createLookupElement(virtualFile: VirtualFile?, editor: Editor, field: PasField): LookupElement {
        val scope = if (field.owner != null) field.owner.name else "-"

        val fieldElement = field.element!!

        var lookupElement = LookupElementBuilder
                .create(fieldElement)
                .withPresentableText(fieldElement.name)

        lookupElement = when (field.fieldType) {
            PasField.FieldType.TYPE -> lookupElement.withIcon(PascalIcons.TYPE)
            PasField.FieldType.UNIT -> lookupElement.withIcon(PascalIcons.UNIT)
            PasField.FieldType.ROUTINE -> lookupElement.withIcon(PascalIcons.ROUTINE)
            PasField.FieldType.CONSTANT -> lookupElement.withIcon(PascalIcons.CONSTANT)
            PasField.FieldType.PROPERTY -> lookupElement.withIcon(PascalIcons.PROPERTY)
            PasField.FieldType.VARIABLE -> lookupElement.withIcon(PascalIcons.VARIABLE)
            PasField.FieldType.PSEUDO_VARIABLE -> lookupElement.withIcon(PascalIcons.VARIABLE)
            null -> lookupElement
        }

        if (field.fieldType == PasField.FieldType.ROUTINE) {


            val routine = fieldElement as? PascalRoutineImpl
            if (routine != null) {
                lookupElement = lookupElement.appendTailText(routine.calculateParameterListPresentation(), false)
            }

            val content = if (fieldElement is PascalRoutineImpl && PsiUtil.hasParameters(fieldElement as PascalRoutineImpl?)) "(" + DocUtil.PLACEHOLDER_CARET + ")" else "()" + DocUtil.PLACEHOLDER_CARET
            lookupElement = lookupElement.withInsertHandler { context, _ ->
                DocUtil.adjustDocument(context.editor, context.editor.caretModel.offset, content)
                val act = ActionManager.getInstance().getAction("ParameterInfo")
                val dataContext = DataManager.getInstance().getDataContext(editor.contentComponent)
                act.actionPerformed(AnActionEvent(null, dataContext, "", act.templatePresentation, ActionManager.getInstance(), 0))
            }
        }

        lookupElement = lookupElement
                .appendTailText(" in " + scope, true)
                .withCaseSensitivity(true)
                .withTypeText(field.calculateTypeText(), false)

        if (field.name.startsWith("__")) {
            return priority(lookupElement, PRIORITY_LOWEST)
        }
        if (field.name.startsWith("_")) {
            return priority(lookupElement, PRIORITY_LOWEST)
        }
        return if (virtualFile != null && virtualFile != field.elementPtr!!.virtualFile) {
            priority(lookupElement, PRIORITY_LOWER)
        } else lookupElement
    }


    private fun priority(element: LookupElement, priority: Double): LookupElement {
        return PrioritizedLookupElement.withPriority(element, priority)
    }

}