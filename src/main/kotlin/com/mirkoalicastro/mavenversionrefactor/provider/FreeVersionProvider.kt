package com.mirkoalicastro.mavenversionrefactor.provider

import com.intellij.psi.xml.XmlTag
import com.mirkoalicastro.mavenversionrefactor.domain.Dependency
import org.apache.xerces.util.XMLChar

class FreeVersionProvider(
    private val propertiesProvider: PropertiesProvider = PropertiesProvider()
) {
    companion object {
        private const val VERSION_SUFFIX = ".version"
        private const val NORMALIZED_PREFIX = "dependency."
        private const val DASH = '-'
        private const val MAX_ATTEMPTS = 5
    }

    fun getFreeVersion(project: XmlTag, dependency: Dependency): String? {
        val properties = propertiesProvider.provide(project)
        val candidate = normalize(dependency.artifactId + VERSION_SUFFIX)
        return if (properties == null || isPropertyAvailable(properties, candidate)) {
            candidate
        } else {
            fallbackWithFullName(properties, dependency)
        }
    }

    private fun fallbackWithFullName(properties: XmlTag, dependency: Dependency): String? {
        val candidate = normalize(dependency.groupId + DASH + dependency.artifactId + VERSION_SUFFIX)
        return if (isPropertyAvailable(properties, candidate)) {
            candidate
        } else {
            val prefix = dependency.groupId + DASH + dependency.artifactId + DASH
            fallbackWithFullNameAndAttempt(properties, prefix, 1)
        }
    }

    private fun fallbackWithFullNameAndAttempt(properties: XmlTag, prefix: String, attempt: Int): String? =
        if (attempt < MAX_ATTEMPTS) {
            val candidate = normalize(prefix + (attempt + 1) + VERSION_SUFFIX)
            if (isPropertyAvailable(properties, candidate)) {
                candidate
            } else {
                fallbackWithFullNameAndAttempt(properties, prefix, attempt + 1)
            }
        } else {
            null
        }

    private fun isPropertyAvailable(properties: XmlTag, property: String) = isValidProperty(property) &&
        properties.children
            .filter { XmlTag::class.java.isInstance(it) }
            .map { XmlTag::class.java.cast(it) }
            .none { property.equals(it.name, ignoreCase = true) }

    private fun normalize(property: String) = if (isValidProperty(property)) {
        property
    } else {
        NORMALIZED_PREFIX + property
    }

    private fun isValidProperty(property: String) = XMLChar.isValidName(property)
}
