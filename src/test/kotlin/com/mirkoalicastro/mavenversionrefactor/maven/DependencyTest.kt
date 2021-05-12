package com.mirkoalicastro.mavenversionrefactor.maven

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify

class DependencyTest : StringSpec({
    val version: Version = mockk()
    val dependency = Dependency("groupId", "artifactId", version)

    "should get version" {
        val dummyValue = "dummyValue"
        every { version.value } returns dummyValue

        val actual = dependency.version

        actual shouldBe dummyValue
        verify { version.value }
    }

    "should set version" {
        val dummyValue = "dummyValue"
        justRun { version.set(dummyValue) }

        dependency.version = dummyValue

        verify(exactly = 1) { version.set(dummyValue) }
    }

})
