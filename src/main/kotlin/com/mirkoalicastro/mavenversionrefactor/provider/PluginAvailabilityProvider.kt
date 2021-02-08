package com.mirkoalicastro.mavenversionrefactor.provider

import com.intellij.psi.PsiElement
import com.mirkoalicastro.mavenversionrefactor.factory.PomFactory

class PluginAvailabilityProvider(
    private val pomFactory: PomFactory = PomFactory(),
    private val freeVersionProvider: FreeVersionProvider = FreeVersionProvider()
) {
    fun provide(psiElement: PsiElement): Boolean {
        val pom = pomFactory.create(psiElement)
        return if (pom != null) {
            freeVersionProvider.getFreeVersion(pom.project, pom.xmlDependency.dependency) != null
        } else {
            false
        }
    }
}
