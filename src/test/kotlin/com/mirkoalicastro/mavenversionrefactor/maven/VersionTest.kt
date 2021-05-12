package com.mirkoalicastro.mavenversionrefactor.maven

import com.intellij.psi.xml.XmlText
import io.kotest.core.spec.style.StringSpec
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify

class VersionTest : StringSpec({
    val tag: XmlText = mockk()

    "should set version on tag" {
        val version = "dummyVersion"
        justRun { tag.value = version }

        Version("value", tag).set(version)

        verify(exactly = 1) { tag.value = version }
    }
})
