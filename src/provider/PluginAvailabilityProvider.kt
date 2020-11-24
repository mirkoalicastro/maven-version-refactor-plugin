package provider

import com.intellij.psi.PsiElement
import factory.PomFactory

class PluginAvailabilityProvider {
    private val pomFactory = PomFactory()
    private val freeVersionProvider = FreeVersionProvider()

    fun provide(psiElement: PsiElement): Boolean {
        val pom = pomFactory.create(psiElement)q
        return if (pom != null) {
            freeVersionProvider.getFreeVersion(pom.project, pom.xmlDependency.dependency) != null
        } else {
            false
        }
    }
}
