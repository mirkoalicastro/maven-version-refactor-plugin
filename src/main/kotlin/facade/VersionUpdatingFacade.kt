package facade

import com.intellij.psi.PsiElement
import domain.Pom
import factory.PomFactory
import log.Logging
import log.logger
import provider.FreeVersionProvider
import updater.PropertiesUpdater

class VersionUpdatingFacade(
        private val freeVersionProvider: FreeVersionProvider = FreeVersionProvider(),
        private val propertiesUpdater: PropertiesUpdater = PropertiesUpdater(),
        private val pomFactory: PomFactory = PomFactory()
) : Logging {
    fun replaceVersion(psiElement: PsiElement) {
        val pom = pomFactory.create(psiElement)
        if (pom != null) {
            doExecute(pom)
        } else {
            logger().error("Unexpected error on {}", psiElement)
        }
    }

    private fun doExecute(pom: Pom) =
            getFreeVersion(pom)?.let {
                replaceVersionWithVariable(pom, it)
                addVersionInProperties(pom, it)
            } ?: logger().error("Unexpected error: cannot retrieve a free version starting from pom {}", pom)

    private fun addVersionInProperties(pom: Pom, freeVersion: String) =
            propertiesUpdater.addVersionToProperties(pom, freeVersion)

    private fun replaceVersionWithVariable(pom: Pom, freeVersion: String) {
        pom.xmlDependency.versionXmlText.value = "\${$freeVersion}"
    }

    private fun getFreeVersion(pom: Pom) =
            freeVersionProvider.getFreeVersion(pom.project, pom.xmlDependency.dependency)
}
