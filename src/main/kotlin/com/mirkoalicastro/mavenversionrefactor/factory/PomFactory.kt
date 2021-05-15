package com.mirkoalicastro.mavenversionrefactor.factory

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.mirkoalicastro.mavenversionrefactor.maven.Dependency
import com.mirkoalicastro.mavenversionrefactor.maven.Pom
import com.mirkoalicastro.mavenversionrefactor.maven.Tag
import com.mirkoalicastro.mavenversionrefactor.maven.Version
import com.mirkoalicastro.mavenversionrefactor.xml.getChildTag
import com.mirkoalicastro.mavenversionrefactor.xml.textValue

private val variablePattern = Regex("^\\s*\\$\\s*\\{.+}\\s*$")

class PomFactory {
    fun create(element: PsiElement) =
        getPomRoot(element)?.let {
            when {
                Tag.Project.value.equals(it.name, ignoreCase = true) -> create(it, element)
                else -> null
            }
        }

    private fun create(root: XmlTag, element: PsiElement): Pom? {
        val eligibleTag = findEligibleTag(element)
        val groupIdTag = eligibleTag?.getChildTag(Tag.GroupId.value)
        val artifactIdTag = eligibleTag?.getChildTag(Tag.ArtifactId.value)
        val versionText = eligibleTag?.getChildTag(Tag.Version.value)?.textValue

        return when {
            groupIdTag == null || artifactIdTag == null || versionText == null -> null
            isVariable(versionText.text) -> null
            else -> {
                val version = Version(versionText.text, versionText)
                val dependency = Dependency(groupIdTag.value.text, artifactIdTag.value.text, version)
                Pom(root, dependency)
            }
        }
    }

    private fun findEligibleTag(element: PsiElement): XmlTag? {
        val firstParent = element.parent
        val secondParent = firstParent?.parent
        val thirdParent = secondParent?.parent
        return listOf(firstParent, secondParent, thirdParent)
            .filterIsInstance<XmlTag>()
            .find { it.name == Tag.Dependency.value || it.name == Tag.Plugin.value }
    }

    private fun isVariable(str: String) =
        str.matches(variablePattern)

    private fun getPomRoot(element: PsiElement) =
        when {
            isPomFile(element) -> (element.containingFile as? XmlFile)?.rootTag
            else -> null
        }

    private fun isPomFile(element: PsiElement) =
        element.containingFile.name.equals("pom.xml", ignoreCase = true)
}
