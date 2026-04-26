package io.github.mirkoalicastro.mavenversionrefactor.provider

import com.intellij.psi.xml.XmlTag
import io.github.mirkoalicastro.mavenversionrefactor.maven.Dependency
import io.github.mirkoalicastro.mavenversionrefactor.maven.Pom
import io.github.mirkoalicastro.mavenversionrefactor.maven.Tag
import io.github.mirkoalicastro.mavenversionrefactor.xml.getChildTag
import org.apache.xerces.util.XMLChar

private const val VERSION_SUFFIX = ".version"
private const val MAX_ATTEMPTS = 3

class VersionNamingProvider {
    fun provide(pom: Pom): String? {
        val properties = pom.project.getChildTag(Tag.Properties.value)
        val candidate = pom.dependency.artifactId + VERSION_SUFFIX
        return if (isPropertyAvailable(properties, candidate)) {
            candidate
        } else {
            fallback(properties, pom.dependency)
        }
    }

    private tailrec fun fallback(
        properties: XmlTag?,
        dependency: Dependency,
        attempt: Int = 0,
    ): String? {
        val attemptSuffix = if (attempt > 0) "-$attempt" else ""
        val candidate = dependency.groupId + "-" + dependency.artifactId + attemptSuffix + VERSION_SUFFIX

        return when {
            isPropertyAvailable(properties, candidate) -> candidate
            attempt < MAX_ATTEMPTS -> fallback(properties, dependency, attempt + 1)
            else -> null
        }
    }

    private fun isPropertyAvailable(
        properties: XmlTag?,
        property: String,
    ) = XMLChar.isValidName(property) && properties?.getChildTag(property) == null
}
