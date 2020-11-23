package factory

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.*
import domain.Dependency
import domain.Pom
import domain.XmlDependency
import domain.constant.XmlNodeName
import java.util.*
import java.util.stream.Collectors

class PomFactory {
    companion object {
        private const val VARIABLE = "^\\s*\\$\\s*\\{.+}\\s*$"
    }

    fun create(currentPsiElement: PsiElement): Pom? {
        val currentXmlToken = castOrNull(currentPsiElement, XmlToken::class.java)
        return if (currentXmlToken != null) {
            create(currentXmlToken)
        } else {
            null
        }
    }

    private fun create(currentXmlToken: XmlToken): Pom? {
        var currentText = castOrNull(currentXmlToken.parent, XmlText::class.java)
        var currentTag = castOrNull(currentXmlToken.parent, XmlTag::class.java)
        if (currentText != null) {
            currentTag = castOrNull(currentText.parent, XmlTag::class.java)
        } else if (currentTag != null) {
            val xmlTexts = extractXmlElement(currentTag, XmlText::class.java)
            if (xmlTexts.size == 1) {
                currentText = xmlTexts[0]
            }
        }
        return if (currentText != null && currentTag != null) {
            createPom(currentText, currentTag)
        } else {
            null
        }
    }

    private fun createPom(version: XmlText, versionTag: XmlTag): Pom? {
        val noAttributesForVersionTag = versionTag.attributes.isEmpty()
        val isVersionTag = XmlNodeName.VERSION.xmlName == versionTag.name
        val dependency = castOrNull(versionTag.parent, XmlTag::class.java)
        var pom: Pom? = null
        if (isVersionTag && noAttributesForVersionTag && dependency != null && !isVariable(version.text)) {
            val project = findBack(dependency, XmlNodeName.PROJECT.xmlName)
            val isDependencyTag = XmlNodeName.DEPENDENCY.xmlName == dependency.name
            val noAttributesForDependencyTag = dependency.attributes.isEmpty()
            if (project != null && isDependencyTag && noAttributesForDependencyTag) {
                pom = createPom(project, dependency, version)
            }
        }
        return pom
    }

    private fun findBack(xmlTag: XmlTag, name: String): XmlTag? =
            castOrNull(xmlTag.parent, XmlTag::class.java)?.let {
                if (name == it.name) {
                    it
                } else {
                    findBack(it, name)
                }
            }

    private fun createPom(project: XmlTag, dependencyTag: XmlTag, versionText: XmlText) =
            createDependency(dependencyTag, versionText.text)?.let {
                val xmlDependency = XmlDependency(it, versionText)
                Pom(dependencyTag.namespace, project, xmlDependency)
            }

    private fun createDependency(dependencyTag: XmlTag, version: String): Dependency? {
        val xmlTagChildren = extractXmlElement(dependencyTag, XmlTag::class.java)
        val groupIdTag = extractXmlTagByName(xmlTagChildren, XmlNodeName.GROUP_ID.xmlName)
        val artifactIdTag = extractXmlTagByName(xmlTagChildren, XmlNodeName.ARTIFACT_ID.xmlName)
        return if (groupIdTag != null && artifactIdTag != null) {
            createDependency(version, groupIdTag, artifactIdTag)
        } else {
            null
        }
    }

    private fun <T : XmlElement?> extractXmlElement(dependencyTag: XmlTag, clazz: Class<T>): List<T> {
        return Arrays.stream(dependencyTag.value.children)
                .filter { obj: XmlTagChild? -> clazz.isInstance(obj) }
                .map { obj: XmlTagChild? -> clazz.cast(obj) }
                .collect(Collectors.toList())
    }

    private fun createDependency(version: String, groupIdTag: XmlTag, artifactIdTag: XmlTag): Dependency {
        val groupId = groupIdTag.value.text
        val artifactId = artifactIdTag.value.text
        return Dependency(
                groupId,
                artifactId,
                version
        )
    }

    private fun isVariable(version: String) =
            version.matches(Regex(VARIABLE))

    private fun extractXmlTagByName(xmlTagChildren: List<XmlTag>, name: String) =
            xmlTagChildren.firstOrNull { name == it.name }

    private fun <T, S : T?> castOrNull(el: T?, clazz: Class<S>?) =
            if (el != null && clazz != null && clazz.isAssignableFrom(el.javaClass)) {
                clazz.cast(el)
            } else {
                null
            }
}
