package com.mirkoalicastro.mavenversionrefactor.xml

import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlTagChild
import com.intellij.psi.xml.XmlText
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk

class ExtensionsKtTest : StringSpec({

    fun createUnderTest(vararg children: XmlTagChild) = mockk<XmlTag>().apply {
        every { this@apply.children } returns children
    }

    fun createXmlTagChild(name: String) = mockk<XmlTag>().apply {
        every { this@apply.name } returns name
    }

    "should return null when node misses children" {
        val underTest = createUnderTest()

        val actual = underTest.getChildTag("test")

        actual shouldBe null
    }

    "should return null when children are not XmlTag" {
        val underTest = createUnderTest(mockk<XmlText>())

        val actual = underTest.getChildTag("test")

        actual shouldBe null
    }

    "should return null when node has children but not the searched one" {
        val underTest = createUnderTest(createXmlTagChild("a"), createXmlTagChild("b"))

        val actual = underTest.getChildTag("c")

        actual shouldBe null
    }

    "should return expected node" {
        val underTest = createUnderTest(createXmlTagChild("a"), createXmlTagChild("b"), mockk<XmlText>())

        val actual = underTest.getChildTag("b")

        actual shouldNotBe null
        actual!!.name shouldBe "b"
    }
})
