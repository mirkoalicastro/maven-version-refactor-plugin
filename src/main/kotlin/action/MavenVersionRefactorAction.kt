package action

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import facade.VersionUpdatingFacade
import provider.PluginAvailabilityProvider

class MavenVersionRefactorAction: PsiElementBaseIntentionAction(), IntentionAction, HighPriorityAction {
    companion object {
        private const val PLUGIN_TEXT = "Refactor version as property"
        private const val FAMILY_NAME = "Maven Version Refactor"
    }

    private val versionUpdatingFacade = VersionUpdatingFacade()
    private val pluginAvailabilityProvider = PluginAvailabilityProvider()

    override fun invoke(project: Project, editor: Editor?, psiElement: PsiElement) =
            versionUpdatingFacade.replaceVersion(psiElement)

    override fun isAvailable(project: Project, editor: Editor?, psiElement: PsiElement): Boolean {
        return pluginAvailabilityProvider.provide(psiElement)
    }

    override fun getText() = PLUGIN_TEXT

    override fun getFamilyName(): @IntentionFamilyName String {
        return FAMILY_NAME
    }
}
