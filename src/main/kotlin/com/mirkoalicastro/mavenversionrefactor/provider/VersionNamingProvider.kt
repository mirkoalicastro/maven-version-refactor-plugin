package com.mirkoalicastro.mavenversionrefactor.provider

import com.intellij.psi.xml.XmlTag
import com.mirkoalicastro.mavenversionrefactor.maven.Dependency
import com.mirkoalicastro.mavenversionrefactor.maven.Tag.Properties
import com.mirkoalicastro.mavenversionrefactor.xml.getChildTag
import org.apache.xerces.util.XMLChar

class VersionNamingProvider {
    companion object {
        private const val versionSuffix = ".version"
        private const val normalizedPrefix = "dependency."
        private const val maxAttempts = 5
    }

    fun provide(project: XmlTag, dependency: Dependency): String? {
        val properties = project.getChildTag(Properties.xmlName)
        val candidate = normalize(dependency.artifactId + versionSuffix)
        return if (properties == null || isPropertyAvailable(properties, candidate)) {
            candidate
        } else {
            fallback(properties, dependency)
        }
    }

    private tailrec fun fallback(properties: XmlTag, dependency: Dependency, attempt: Int = 0): String? {
        val attemptSuffix = if (attempt > 0) "-$attempt" else ""
        val candidate = normalize(dependency.groupId + "-" + dependency.artifactId + attemptSuffix + versionSuffix)

        return when {
            isPropertyAvailable(properties, candidate) -> candidate
            attempt < maxAttempts -> fallback(properties, dependency, attempt + 1)
            else -> null
        }
    }

    private fun isPropertyAvailable(properties: XmlTag, property: String) =
        properties.children
            .filter { XmlTag::class.java.isInstance(it) }
            .map { XmlTag::class.java.cast(it) }
            .none { property.equals(it.name, ignoreCase = true) }

    private fun normalize(property: String) =
        if (XMLChar.isValidName(property)) {
            property
        } else {
            normalizedPrefix + property
        }
}
