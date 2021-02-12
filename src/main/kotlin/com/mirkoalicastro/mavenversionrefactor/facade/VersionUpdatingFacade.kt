package com.mirkoalicastro.mavenversionrefactor.facade

import com.intellij.psi.PsiElement
import com.mirkoalicastro.mavenversionrefactor.domain.Pom
import com.mirkoalicastro.mavenversionrefactor.factory.PomFactory
import com.mirkoalicastro.mavenversionrefactor.log.Logging
import com.mirkoalicastro.mavenversionrefactor.log.logger
import com.mirkoalicastro.mavenversionrefactor.provider.FreeVersionProvider
import com.mirkoalicastro.mavenversionrefactor.updater.PropertiesUpdater

class VersionUpdatingFacade(
    private val freeVersionProvider: FreeVersionProvider = FreeVersionProvider(),
    private val propertiesUpdater: PropertiesUpdater = PropertiesUpdater(),
    private val pomFactory: PomFactory = PomFactory()
) : Logging {
    fun replaceVersion(psiElement: PsiElement) = pomFactory.create(psiElement)?.let {
        doExecute(it)
    } ?: logger().error("Unexpected error on {}", psiElement)

    private fun doExecute(pom: Pom) =
        getFreeVersion(pom)?.let {
            replaceVersionWithVariable(pom, it)
            addVersionInProperties(pom, it)
        } ?: logger().error("Unexpected error: cannot retrieve a free version starting from POM {}", pom)

    private fun addVersionInProperties(pom: Pom, freeVersion: String) =
        propertiesUpdater.addVersionToProperties(pom, freeVersion)

    private fun replaceVersionWithVariable(pom: Pom, freeVersion: String) {
        pom.xmlDependency.versionXmlText.value = "\${$freeVersion}"
    }

    private fun getFreeVersion(pom: Pom) =
        freeVersionProvider.getFreeVersion(pom.project, pom.xmlDependency.dependency)
}
