package com.mirkoalicastro.mavenversionrefactor.action

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.mirkoalicastro.mavenversionrefactor.factory.PomFactory
import com.mirkoalicastro.mavenversionrefactor.maven.Pom
import com.mirkoalicastro.mavenversionrefactor.provider.VersionNamingProvider

class MavenVersionRefactorAction(
    private val versionNamingProvider: VersionNamingProvider = VersionNamingProvider(),
    private val pomFactory: PomFactory = PomFactory()
) : PsiElementBaseIntentionAction(), IntentionAction, HighPriorityAction {

    override fun invoke(project: Project, editor: Editor?, psiElement: PsiElement) {
        val pom = pomFactory.create(psiElement)
        pom?.let(::getFreeVersion)?.let {
            pom.addVersion(it)
        }
    }

    override fun isAvailable(project: Project, editor: Editor?, psiElement: PsiElement) =
        pomFactory.create(psiElement)?.let(::getFreeVersion) != null

    override fun getText() = "Refactor version as property"

    override fun getFamilyName() = "Maven Version Refactor"

    private fun getFreeVersion(pom: Pom) =
        versionNamingProvider.provide(pom.project, pom.dependency)
}
