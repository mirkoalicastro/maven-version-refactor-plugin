package com.mirkoalicastro.mavenversionrefactor.factory

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.mirkoalicastro.mavenversionrefactor.maven.Dependency
import com.mirkoalicastro.mavenversionrefactor.maven.Pom
import com.mirkoalicastro.mavenversionrefactor.maven.Tag
import com.mirkoalicastro.mavenversionrefactor.maven.Version
import com.mirkoalicastro.mavenversionrefactor.xml.getChildTag

private val VAR_REGEX = Regex("^\\s*\\$\\s*\\{.+}\\s*$")

class PomFactory {
    fun create(element: PsiElement): Pom? {
        val root = if (isPomFile(element)) getRoot(element) else null
        return if (root != null && Tag.Project.xmlName.equals(root.name, ignoreCase = true)) {
            val eligibleTag = findEligibleTag(element)
            val groupIdTag = eligibleTag?.getChildTag(Tag.GroupId.xmlName)
            val artifactIdTag = eligibleTag?.getChildTag(Tag.ArtifactId.xmlName)
            val versionTag = eligibleTag?.getChildTag(Tag.Version.xmlName)
            val versionText = versionTag?.value?.textElements?.getOrNull(0)

            when {
                groupIdTag == null || artifactIdTag == null || versionText == null -> null
                isVariable(versionTag) -> null
                else -> {
                    val version = Version(versionTag.value.text, versionText)
                    val dependency = Dependency(groupIdTag.value.text, artifactIdTag.value.text, version)
                    Pom(root, dependency)
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

    private fun isVariable(tag: XmlTag) = tag.value.text.matches(VAR_REGEX)

    private fun getRoot(element: PsiElement) = (element.containingFile as? XmlFile)?.rootTag

    private fun isPomFile(element: PsiElement) = element.containingFile.name.equals("pom.xml", ignoreCase = true)
}
