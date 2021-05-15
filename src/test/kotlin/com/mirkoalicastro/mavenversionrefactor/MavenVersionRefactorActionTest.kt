package com.mirkoalicastro.mavenversionrefactor

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.mirkoalicastro.mavenversionrefactor.factory.PomFactory
import com.mirkoalicastro.mavenversionrefactor.maven.Pom
import com.mirkoalicastro.mavenversionrefactor.provider.VersionNamingProvider
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import io.mockk.called
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify

class MavenVersionRefactorActionTest : StringSpec({
    val versionNamingProvider = mockk<VersionNamingProvider>()
    val pomFactory = mockk<PomFactory>()
    val psiElement = mockk<PsiElement>()
    val project = mockk<Project>()
    val editor = mockk<Editor>()

    val pom = mockk<Pom>()

    val underTest = createUnderTest(pomFactory, versionNamingProvider)

    afterTest {
        clearAllMocks()
    }

    "should do nothing when Pom misses" {
        every { pomFactory.create(psiElement) } returns null

        underTest.invoke(project, editor, psiElement)

        verify { listOf(project, editor, versionNamingProvider) wasNot called }
    }

    "should do nothing when version refactor is not possible" {
        every { pomFactory.create(psiElement) } returns pom
        every { versionNamingProvider.provide(pom) } returns null

        underTest.invoke(project, editor, psiElement)

        verify { listOf(project, editor) wasNot called }
    }

    "should add version when version refactor is possible" {
        val version = "version123"
        every { pomFactory.create(psiElement) } returns pom
        every { versionNamingProvider.provide(pom) } returns version
        justRun { pom.addVersion(version) }

        underTest.invoke(project, editor, psiElement)

        verify {
            listOf(project, editor) wasNot called
            pom.addVersion(version)
        }
    }

    "should be not available when Pom misses" {
        every { pomFactory.create(psiElement) } returns null

        val actual = underTest.isAvailable(project, editor, psiElement)

        actual shouldBe false
        verify { listOf(project, editor, versionNamingProvider) wasNot called }
    }

    "should be available when version refactor is possible" {
        table(
            headers("version", "available"),
            row(null, false),
            row("dummyVersion", true)
        ).forAll { version, expected ->
            every { pomFactory.create(psiElement) } returns pom
            every { versionNamingProvider.provide(pom) } returns version

            val actual = underTest.isAvailable(project, editor, psiElement)

            actual shouldBe expected
            verify { listOf(project, editor) wasNot called }
        }
    }

    "should have expected text" {
        underTest.text shouldBe "Refactor version as property"
    }

    "should have expected family name" {
        underTest.familyName shouldBe "Maven Version Refactor"
    }
})

private fun createUnderTest(pomFactory: PomFactory, versionNamingProvider: VersionNamingProvider) =
    MavenVersionRefactorAction().apply {
        overrideField("pomFactory", pomFactory)
        overrideField("versionNamingProvider", versionNamingProvider)
    }

private fun MavenVersionRefactorAction.overrideField(name: String, value: Any) =
    MavenVersionRefactorAction::class.java.declaredFields
        .firstOrNull { it.name == name }
        ?.apply { isAccessible = true }
        ?.set(this, value)
