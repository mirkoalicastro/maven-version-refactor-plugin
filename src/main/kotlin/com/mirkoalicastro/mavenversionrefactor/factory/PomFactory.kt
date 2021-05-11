package com.mirkoalicastro.mavenversionrefactor.factory

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.mirkoalicastro.mavenversionrefactor.domain.Dependency
import com.mirkoalicastro.mavenversionrefactor.domain.Pom
import com.mirkoalicastro.mavenversionrefactor.domain.Tag
import com.mirkoalicastro.mavenversionrefactor.domain.Version
import com.mirkoalicastro.mavenversionrefactor.xml.getChildTag

private val variableRegex = Regex("^\\s*\\$\\s*\\{.+}\\s*$")

class PomFactory {
    fun create(element: PsiElement): Pom? {
        val root = if (isPomFile(element)) getRoot(element) else null
        return if (root != null && Tag.Project.xmlName.equals(root.name, ignoreCase = true)) {
            val eligible = findEligibleTag(element)
            val groupId = eligible?.getChildTag(Tag.GroupId.xmlName)
            val artifactId = eligible?.getChildTag(Tag.ArtifactId.xmlName)
            val version = eligible?.getChildTag(Tag.Version.xmlName)
            val versionText = version?.value?.textElements?.getOrNull(0)

            when {
                groupId == null || artifactId == null || versionText == null -> null
                isNotVar(version) -> null
                else -> {
                    val dep = Dependency(groupId.value.text, artifactId.value.text, Version(version.value.text, versionText))
                    Pom(root, dep)
                }
            }
        } else {
            null
        }
    }

    private fun findEligibleTag(element: PsiElement) =
        listOf(element.parent, element.parent?.parent, element.parent?.parent?.parent)
            .filterIsInstance<XmlTag>()
            .find { it.name == Tag.Dependency.xmlName || it.name == Tag.Plugin.xmlName }

    private fun isNotVar(tag: XmlTag) = !tag.value.text.matches(variableRegex)

    private fun getRoot(element: PsiElement) = (element.containingFile as? XmlFile)?.rootTag

    private fun isPomFile(element: PsiElement) = element.containingFile.name.equals("pom.xml", ignoreCase = true)
}
