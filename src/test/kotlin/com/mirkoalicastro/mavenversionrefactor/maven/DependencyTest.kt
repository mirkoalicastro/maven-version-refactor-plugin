package com.mirkoalicastro.mavenversionrefactor.maven

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify

class DependencyTest : StringSpec({
    val version: Version = mockk()
    val underTest = Dependency("groupId", "artifactId", version)

    afterTest {
        clearAllMocks()
    }

    "should get members" {
        every { version.value } returns "dummyValue"

        assertSoftly {
            underTest.groupId shouldBe "groupId"
            underTest.artifactId shouldBe "artifactId"
            underTest.version shouldBe "dummyValue"
        }
        verify { version.value }
    }

    "should set version" {
        val dummyValue = "dummyValue"
        justRun { version.set(dummyValue) }

        underTest.version = dummyValue

        verify(exactly = 1) { version.set(dummyValue) }
    }
})
