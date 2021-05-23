package com.mirkoalicastro.mavenversionrefactor.provider

import com.intellij.psi.xml.XmlTag
import com.mirkoalicastro.mavenversionrefactor.maven.Dependency
import com.mirkoalicastro.mavenversionrefactor.maven.Pom
import com.mirkoalicastro.mavenversionrefactor.xml.getChildTag
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

class VersionNamingProviderTest : StringSpec({
    val groupId = "dummyGroupId"
    val artifactId = "dummyArtifactId"
    val pom = mockk<Pom>()
    val project = mockk<XmlTag>()
    val properties = mockk<XmlTag>()
    val property = mockk<XmlTag>()
    val dependency = Dependency(groupId, artifactId, mockk())
    val underTest = VersionNamingProvider()

    beforeTest {
        mockkStatic("com.mirkoalicastro.mavenversionrefactor.xml.Extensions")
    }

    afterTest {
        clearAllMocks()
    }

    "should return artifactId.version when properties miss" {
        every { pom.project } returns project
        every { project.getChildTag("properties") } returns null
        every { pom.dependency } returns dependency

        val actual = underTest.provide(pom)

        actual shouldBe "$artifactId.version"
    }

    "should return artifactId.version when properties do not contain that property" {
        every { pom.project } returns project
        every { project.getChildTag("properties") } returns properties
        every { pom.dependency } returns dependency
        every { properties.getChildTag("$artifactId.version") } returns null

        val actual = underTest.provide(pom)

        actual shouldBe "$artifactId.version"
    }

    "should return groupId-artifactId.version when possible" {
        every { pom.project } returns project
        every { project.getChildTag("properties") } returns properties
        every { pom.dependency } returns dependency
        every { properties.getChildTag("$artifactId.version") } returns property
        every { properties.getChildTag("$groupId-$artifactId.version") } returns null

        val actual = underTest.provide(pom)

        actual shouldBe "$groupId-$artifactId.version"
    }

    "should return groupId-artifactId-attempt.version when possible" {
        table(
            headers("times", "expected"),
            row(0, "$groupId-$artifactId-1.version"),
            row(1, "$groupId-$artifactId-2.version"),
            row(2, "$groupId-$artifactId-3.version")
        ).forAll { times, expected ->
            every { pom.project } returns project
            every { project.getChildTag("properties") } returns properties
            every { pom.dependency } returns dependency
            every { properties.getChildTag("$artifactId.version") } returns property
            every { properties.getChildTag("$groupId-$artifactId.version") } returns property
            repeat(times) {
                every { properties.getChildTag("$groupId-$artifactId-${it + 1}.version") } returns property
            }
            every { properties.getChildTag("$groupId-$artifactId-${times + 1}.version") } returns null

            val actual = underTest.provide(pom)

            actual shouldBe expected
        }
    }

    "should return null after all attempts" {
        every { pom.project } returns project
        every { project.getChildTag("properties") } returns properties
        every { pom.dependency } returns dependency
        every { properties.getChildTag("$artifactId.version") } returns property
        every { properties.getChildTag("$groupId-$artifactId.version") } returns property
        repeat(3) {
            every { properties.getChildTag("$groupId-$artifactId-${it + 1}.version") } returns property
        }

        val actual = underTest.provide(pom)

        actual shouldBe null
    }

    "should return null when is not a valid XML name" {
        val invalidArtifactId = "/"
        val invalidGroupId = "."
        every { pom.project } returns project
        every { project.getChildTag("properties") } returns properties
        every { pom.dependency } returns Dependency(invalidGroupId, invalidArtifactId, mockk())

        val actual = underTest.provide(pom)

        actual shouldBe null
    }
})
