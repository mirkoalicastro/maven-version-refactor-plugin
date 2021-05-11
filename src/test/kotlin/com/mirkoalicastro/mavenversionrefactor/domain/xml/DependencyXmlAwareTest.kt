package com.mirkoalicastro.mavenversionrefactor.domain.xml

import com.intellij.psi.xml.XmlText
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearAllMocks
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify

class DependencyXmlAwareTest : BehaviorSpec({
    val versionXmlText: XmlText = mockk()
    val underTest = RefactoringDependency(mockk(), versionXmlText)

    afterTest {
        clearAllMocks()
    }

    Given("a dummy version") {
        val version = "dummy version"
        justRun { versionXmlText.value = version }
        When("setting the version") {
            underTest.setVersion(version)
            Then("version should be set") {
                verify(exactly = 1) { versionXmlText.value = version }
            }
        }
    }
})
