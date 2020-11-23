package provider

import com.intellij.psi.PsiElement
import factory.PomFactory

class PluginAvailabilityProvider {
    private val pomFactory = PomFactory()

    fun provide(psiElement: PsiElement) =
            pomFactory.create(psiElement) != null
}
