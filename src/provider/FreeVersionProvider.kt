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
        val candidate = dependency.groupId + DASH + dependency.artifactId + VERSION_SUFFIX
        return if (!isPropertyInUse(properties, candidate)) {
            candidate
        } else {
            val prefix = dependency.groupId + DASH + dependency.artifactId + DASH
            fallbackWithFullNameAndAttempt(properties, prefix, 1)
        }
    }

    private fun fallbackWithFullNameAndAttempt(properties: XmlTag, prefix: String, attempt: Int): String? =
            if (attempt <= MAX_ATTEMPTS) {
                val candidate = prefix + (attempt+1) + VERSION_SUFFIX
                if (!isPropertyInUse(properties, candidate)) {
                    candidate
                } else {
                    fallbackWithFullNameAndAttempt(properties, prefix, attempt+1)
                }
            } else {
                null
            }

    private fun isPropertyInUse(properties: XmlTag, property: String) =
            properties.children
                    .filter { XmlTag::class.java.isInstance(it) }
                    .map { XmlTag::class.java.cast(it) }
                    .any { property.toLowerCase() == it.name.toLowerCase() }
}
