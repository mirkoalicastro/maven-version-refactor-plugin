package com.mirkoalicastro.mavenversionrefactor.factory

import com.intellij.json.psi.JsonFile
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlTagValue
import com.intellij.psi.xml.XmlText
import com.mirkoalicastro.mavenversionrefactor.maven.Dependency
import com.mirkoalicastro.mavenversionrefactor.maven.Pom
import com.mirkoalicastro.mavenversionrefactor.maven.Version
import com.mirkoalicastro.mavenversionrefactor.xml.getChildTag
import com.mirkoalicastro.mavenversionrefactor.xml.textValue
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic

class PomFactoryTest : StringSpec({
    val element = mockk<PsiElement>()
    val containingFile = mockk<XmlFile>()
    val rootTag = mockk<XmlTag>()
    val parent = mockk<XmlTag>()
    val groupIdTag = mockk<XmlTag>()
    val artifactIdTag = mockk<XmlTag>()
    val versionTag = mockk<XmlTag>()
    val versionText = mockk<XmlText>()

    val underTest = PomFactory()

    beforeTest {
        mockkStatic(XmlTag::getChildTag)
        mockkStatic(XmlTag::textValue)
    }

    afterTest {
        clearAllMocks()
    }

    "should return null when file type is not XML" {
        every { element.containingFile } returns mockk<JsonFile>().apply {
            every { name } returns "pom.xml"
        }

        val actual = underTest.create(element)

        actual shouldBe null
    }

    "should return null when file name is not pom.xml" {
        every { element.containingFile } returns containingFile
        every { containingFile.name } returns "tree.xml"

        val actual = underTest.create(element)

        actual shouldBe null
    }

    "should return null when file is empty" {
        every { element.containingFile } returns containingFile
        every { containingFile.name } returns "pom.xml"
        every { containingFile.rootTag } returns null

        val actual = underTest.create(element)

        actual shouldBe null
    }

    "should return null when root tag is not named project" {
        every { element.containingFile } returns containingFile
        every { containingFile.name } returns "pom.xml"
        every { containingFile.rootTag } returns rootTag
        every { rootTag.name } returns "other"

        val actual = underTest.create(element)

        actual shouldBe null
    }

    "should return null when parents are all ineligible" {
        every { element.containingFile } returns containingFile
        every { containingFile.name } returns "pom.xml"
        every { containingFile.rootTag } returns rootTag
        every { rootTag.name } returns "project"
        every { element.parent } returns parent
        every { parent.parent } returns parent
        every { parent.name } returns "invalid"

        val actual = underTest.create(element)

        actual shouldBe null
    }

    "should return null when some parents are null and others ineligible" {
        table(
            headers("parents"),
            row(listOf(null, null, null)),
            row(listOf(element, null, null)),
            row(listOf(element, element, null))
        ).forAll { parents ->
            every { element.containingFile } returns containingFile
            every { containingFile.name } returns "pom.xml"
            every { containingFile.rootTag } returns rootTag
            every { rootTag.name } returns "project"
            every { element.parent } returnsMany parents

            val actual = underTest.create(element)

            actual shouldBe null
        }
    }

    "should return null when parent is eligible but missing data" {
        table(
            headers("eligibleTagName", "groupIdTag", "artifactIdTag", "versionTag", "versionText"),
            row("plugin", null, artifactIdTag, versionTag, versionText),
            row("dependency", null, artifactIdTag, versionTag, versionText),
            row("plugin", groupIdTag, null, versionTag, versionText),
            row("dependency", groupIdTag, null, versionTag, versionText),
            row("plugin", groupIdTag, artifactIdTag, null, null),
            row("dependency", groupIdTag, artifactIdTag, null, null),
            row("plugin", groupIdTag, artifactIdTag, versionTag, null),
            row("dependency", groupIdTag, artifactIdTag, versionTag, null),
        ).forAll { name, groupIdTag, artifactIdTag, versionTag, versionText ->
            every { element.containingFile } returns containingFile
            every { containingFile.name } returns "pom.xml"
            every { containingFile.rootTag } returns rootTag
            every { rootTag.name } returns "project"
            every { element.parent } returns parent
            every { parent.parent } returns parent
            every { parent.name } returns name
            every { parent.getChildTag("groupId") } returns groupIdTag
            every { parent.getChildTag("artifactId") } returns artifactIdTag
            every { parent.getChildTag("version") } returns versionTag
            if (versionTag != null) {
                every { versionTag.textValue } returns versionText
            }

            val actual = underTest.create(element)

            actual shouldBe null
        }
    }

    "should return null when version is already a variable" {
        table(
            headers("eligibleTagName", "actualVersion"),
            row("dependency", "\${version}"),
            row("plugin", "\${test.version}")
        ).forAll { name, version ->
            every { element.containingFile } returns containingFile
            every { containingFile.name } returns "pom.xml"
            every { containingFile.rootTag } returns rootTag
            every { rootTag.name } returns "project"
            every { element.parent } returns parent
            every { parent.parent } returns parent
            every { parent.name } returns name
            every { parent.getChildTag("groupId") } returns groupIdTag
            every { parent.getChildTag("artifactId") } returns artifactIdTag
            every { parent.getChildTag("version") } returns versionTag
            every { versionTag.textValue } returns versionText
            every { versionText.text } returns version

            val actual = underTest.create(element)

            actual shouldBe null
        }
    }

    "should create Pom when needed" {
        table(
            headers("eligibleTagName"),
            row("dependency"),
            row("plugin")
        ).forAll { name ->
            every { element.containingFile } returns containingFile
            every { containingFile.name } returns "pom.xml"
            every { containingFile.rootTag } returns rootTag
            every { rootTag.name } returns "project"
            every { element.parent } returns parent
            every { parent.parent } returns parent
            every { parent.name } returns name
            every { parent.getChildTag("groupId") } returns groupIdTag
            every { parent.getChildTag("artifactId") } returns artifactIdTag
            every { parent.getChildTag("version") } returns versionTag
            every { versionTag.textValue } returns versionText
            every { versionText.text } returns "123"
            every { groupIdTag.value } returns mockk<XmlTagValue>().apply {
                every { text } returns "groupId"
            }
            every { artifactIdTag.value } returns mockk<XmlTagValue>().apply {
                every { text } returns "artifactId"
            }

            val actual = underTest.create(element)

            val dependency = Dependency("groupId", "artifactId", Version("123", versionText))
            actual shouldBe Pom(rootTag, dependency)
        }
    }
})
