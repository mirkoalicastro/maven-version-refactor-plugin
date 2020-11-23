package provider

import com.intellij.psi.xml.XmlTag
import domain.Dependency

class FreeVersionProvider {
    companion object {
        private const val VERSION_SUFFIX = ".version"
        private const val DASH = '-'
        private const val MAX_ATTEMPTS = 20
    }

    private val propertiesProvider = PropertiesProvider()

    fun getFreeVersion(project: XmlTag, dependency: Dependency): String? {
        val properties = propertiesProvider.provide(project)
        val candidate = dependency.artifactId + VERSION_SUFFIX
        return if (properties == null || !isPropertyInUse(properties, candidate)) {
            candidate
        } else {
            fallbackWithFullName(properties, dependency)
        }
    }

    private fun fallbackWithFullName(properties: XmlTag, dependency: Dependency): String? {
        var candidate = dependency.groupId + DASH + dependency.artifactId + VERSION_SUFFIX
        var attempt = 1
        while (isPropertyInUse(properties, candidate) && attempt < MAX_ATTEMPTS) {
            candidate = dependency.groupId + DASH + dependency.artifactId + DASH + (attempt + 1) + VERSION_SUFFIX
            attempt++
        }
        return if (!isPropertyInUse(properties, candidate)) {
            candidate
        } else {
            null
        }
    }

    private fun isPropertyInUse(properties: XmlTag, property: String) =
            properties.children
                    .filter { XmlTag::class.java.isInstance(it) }
                    .map { XmlTag::class.java.cast(it) }
                    .any { property.toLowerCase() == it.name.toLowerCase() }
}
