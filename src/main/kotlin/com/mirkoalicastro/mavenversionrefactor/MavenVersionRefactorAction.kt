package com.mirkoalicastro.mavenversionrefactor

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.mirkoalicastro.mavenversionrefactor.factory.PomFactory
import com.mirkoalicastro.mavenversionrefactor.provider.VersionNamingProvider

class MavenVersionRefactorAction : PsiElementBaseIntentionAction(), IntentionAction, HighPriorityAction {

    private val pomFactory = PomFactory()
    private val versionNamingProvider = VersionNamingProvider()

    override fun invoke(project: Project, editor: Editor?, psiElement: PsiElement) {
        val pom = pomFactory.create(psiElement)
        pom?.let(versionNamingProvider::provide)?.let(pom::addVersion)
    }

    override fun isAvailable(project: Project, editor: Editor?, psiElement: PsiElement) =
        pomFactory.create(psiElement)?.let(versionNamingProvider::provide) != null

    override fun getText() = "Refactor version as property"

    override fun getFamilyName() = "Maven Version Refactor"
}
