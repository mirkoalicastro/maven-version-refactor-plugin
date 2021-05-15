package com.mirkoalicastro.mavenversionrefactor.maven

import com.intellij.psi.xml.XmlText
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify

class VersionTest : StringSpec({
    val version = "dummyVersion"

    val tag = mockk<XmlText>()
    val underTest = Version("value", tag)

    "should get members" {
        assertSoftly {
            underTest.value shouldBe "value"
            underTest.tag shouldBe tag
        }
    }

    "should set version on tag" {
        justRun { tag.value = version }

        underTest.set(version)

        verify(exactly = 1) { tag.value = version }
    }
})
