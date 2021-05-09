package com.mirkoalicastro.mavenversionrefactor.factory

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.mirkoalicastro.mavenversionrefactor.domain.Dependency
import com.mirkoalicastro.mavenversionrefactor.domain.xml.DependencyXmlAware
import com.mirkoalicastro.mavenversionrefactor.domain.xml.PomXmlAware
import com.mirkoalicastro.mavenversionrefactor.domain.xml.XmlNodeName.ARTIFACT_ID
import com.mirkoalicastro.mavenversionrefactor.domain.xml.XmlNodeName.DEPENDENCY
import com.mirkoalicastro.mavenversionrefactor.domain.xml.XmlNodeName.GROUP_ID
import com.mirkoalicastro.mavenversionrefactor.domain.xml.XmlNodeName.PLUGIN
import com.mirkoalicastro.mavenversionrefactor.domain.xml.XmlNodeName.PROJECT
import com.mirkoalicastro.mavenversionrefactor.domain.xml.XmlNodeName.VERSION
import com.mirkoalicastro.mavenversionrefactor.xml.getTag
import com.mirkoalicastro.mavenversionrefactor.xml.getTags

private val variableRegex = Regex("^\\s*\\$\\s*\\{.+}\\s*$")

class PomFactory {
    fun create(element: PsiElement): PomXmlAware? {
        val root = if (isPomFile(element)) getRoot(element) else null
        if (root != null && PROJECT.xmlName.equals(root.name, ignoreCase = true)) {
            val children = findEligibleTag(element)?.getTags()
            val groupId = children?.getTag(GROUP_ID.xmlName)
            val artifactId = children?.getTag(ARTIFACT_ID.xmlName)
            val version = children?.getTag(VERSION.xmlName)
            val versionText = version?.value?.textElements?.getOrNull(0)

            if (groupId != null && artifactId != null && version != null && versionText != null && isNotVar(version)) {
                val dependency = Dependency(groupId.value.text, artifactId.value.text, version.value.text)
                return PomXmlAware(root, DependencyXmlAware(dependency, versionText))
            }
        }
        return null
    }

    private fun findEligibleTag(element: PsiElement) =
        listOf(element.parent, element.parent?.parent, element.parent?.parent?.parent)
            .filterIsInstance<XmlTag>()
            .find { it.name == DEPENDENCY.xmlName || it.name == PLUGIN.xmlName }

    private fun isNotVar(tag: XmlTag) = !tag.value.text.matches(variableRegex)

    private fun getRoot(element: PsiElement) = (element.containingFile as? XmlFile)?.rootTag

    private fun isPomFile(element: PsiElement) = element.containingFile.name.equals("pom.xml", ignoreCase = true)
}
