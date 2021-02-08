package com.mirkoalicastro.mavenversionrefactor.factory

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlElement
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlText
import com.intellij.psi.xml.XmlToken
import com.mirkoalicastro.mavenversionrefactor.domain.Dependency
import com.mirkoalicastro.mavenversionrefactor.domain.Pom
import com.mirkoalicastro.mavenversionrefactor.domain.XmlDependency
import com.mirkoalicastro.mavenversionrefactor.domain.constant.XmlNodeName

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
        val (versionText, versionTag) = extractVersionTextAndTag(currentXmlToken)
        return if (versionText != null && versionTag != null) {
            createPom(versionText, versionTag)
        } else {
            null
        }
    }

    private fun extractVersionTextAndTag(currentXmlToken: XmlToken): Pair<XmlText?, XmlTag?> {
        val currentText = castOrNull(currentXmlToken.parent, XmlText::class.java)
        val tag = castOrNull(currentText?.parent, XmlTag::class.java)
        return if (currentText != null && tag != null) {
            currentText to tag
        } else {
            val currentTag = castOrNull(currentXmlToken.parent, XmlTag::class.java)
            val text = extractXmlElement(currentTag, XmlText::class.java).getOrNull(0)
            text to currentTag
        }
    }

    private fun createPom(version: XmlText, versionTag: XmlTag): Pom? {
        val noAttributesForVersionTag = versionTag.attributes.isEmpty()
        val isVersionTag = XmlNodeName.VERSION.xmlName == versionTag.name
        val dependency = castOrNull(versionTag.parent, XmlTag::class.java)
        return if (isVersionTag && noAttributesForVersionTag && dependency != null && !isVariable(version.text)) {
            createPom(dependency, version)
        } else {
            null
        }
    }

    private fun createPom(dependency: XmlTag, version: XmlText): Pom? {
        val project = findBack(dependency, XmlNodeName.PROJECT.xmlName)
        val isDependencyTag = XmlNodeName.DEPENDENCY.xmlName == dependency.name
        val noAttributesForDependencyTag = dependency.attributes.isEmpty()
        return if (project != null && isDependencyTag && noAttributesForDependencyTag) {
            createPom(project, dependency, version)
        } else {
            null
        }
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
            Pom(dependencyTag.namespace, project, XmlDependency(it, versionText))
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

    private fun <T : XmlElement> extractXmlElement(dependencyTag: XmlTag?, clazz: Class<T>) =
        dependencyTag?.value?.children
            ?.filter { clazz.isInstance(it) }
            ?.map { clazz.cast(it) }
            ?: emptyList()

    private fun createDependency(version: String, groupIdTag: XmlTag, artifactIdTag: XmlTag) =
        Dependency(groupIdTag.value.text, artifactIdTag.value.text, version)

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
