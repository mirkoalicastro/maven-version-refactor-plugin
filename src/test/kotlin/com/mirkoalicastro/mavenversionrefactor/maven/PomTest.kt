package com.mirkoalicastro.mavenversionrefactor.maven

import com.intellij.psi.xml.XmlTag
import com.mirkoalicastro.mavenversionrefactor.xml.getChildTag
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify

class PomTest : StringSpec({
    val name = "dummyName"
    val namespace = "dummyNamespace"
    val version = "dummyVersion"

    val project = mockk<XmlTag>()
    val dependency = mockk<Dependency>()
    val properties = mockk<XmlTag>()
    val property = mockk<XmlTag>()

    val underTest = Pom(project, dependency)

    fun mockDirectFlow() {
        every { properties.namespace } returns namespace
        every { dependency.version } returns version
        every { properties.createChildTag(name, namespace, version, true) } returns property
        every { properties.addSubTag(property, true) } returns property
        justRun { dependency.version = "\${$name}" }
    }

    fun verifyDirectFlow() = verify {
        project.getChildTag("properties")
        properties.namespace
        dependency.version
        properties.createChildTag(name, namespace, version, true)
        properties.addSubTag(property, true)
        dependency.version = "\${$name}"
    }

    beforeTest {
        mockkStatic("com.mirkoalicastro.mavenversionrefactor.xml.Extensions")
    }

    afterTest {
        clearAllMocks()
    }

    "should add version when properties already exist" {
        every { project.getChildTag("properties") } returns properties
        mockDirectFlow()

        underTest.addVersion(name)

        verifyDirectFlow()
    }

    "should add version to new properties when properties do not exist" {
        every { project.getChildTag("properties") } returnsMany listOf(null, properties)
        every { project.namespace } returns namespace
        every { project.createChildTag("properties", namespace, "", true) } returns properties
        every { project.addSubTag(properties, true) } returns properties
        mockDirectFlow()

        underTest.addVersion(name)

        verify {
            project.getChildTag("properties")
            project.namespace
            project.createChildTag("properties", namespace, "", true)
            project.addSubTag(properties, true)
        }
        verifyDirectFlow()
    }

    "should get members" {
        assertSoftly {
            underTest.project shouldBe project
            underTest.dependency shouldBe dependency
        }
    }
})
