package facade

import com.intellij.psi.PsiElement
import domain.Pom
import factory.PomFactory
import org.slf4j.LoggerFactory
import provider.FreeVersionProvider
import updater.PropertiesUpdater

class VersionUpdatingFacade {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(VersionUpdatingFacade::class.java)
    }

    private val freeVersionProvider = FreeVersionProvider()
    private val propertiesUpdater = PropertiesUpdater()
    private val pomFactory = PomFactory()

    fun replaceVersion(psiElement: PsiElement) {
        val pom = pomFactory.create(psiElement)
        if (pom != null) {
            doExecute(pom)
        } else {
            LOGGER.error("Unexpected error on {}", psiElement)
        }
    }

    private fun doExecute(pom: Pom) =
            getFreeVersion(pom)?.let {
                replaceVersionWithVariable(pom, it)
                addVersionInProperties(pom, it)
            } ?: LOGGER.error("Unexpected error: cannot retrieve a free version starting from pom {}", pom)

    private fun addVersionInProperties(pom: Pom, freeVersion: String) =
            propertiesUpdater.addVersionToProperties(pom, freeVersion)

    private fun replaceVersionWithVariable(pom: Pom, freeVersion: String) {
        pom.xmlDependency.versionXmlText.value = "\${$freeVersion}"
    }

    private fun getFreeVersion(pom: Pom) =
            freeVersionProvider.getFreeVersion(pom.project, pom.xmlDependency.dependency)
}
