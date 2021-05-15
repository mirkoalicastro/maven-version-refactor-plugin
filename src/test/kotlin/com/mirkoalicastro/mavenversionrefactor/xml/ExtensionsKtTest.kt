package com.mirkoalicastro.mavenversionrefactor.xml

import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlTagChild
import com.intellij.psi.xml.XmlTagValue
import com.intellij.psi.xml.XmlText
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk

class ExtensionsKtTest : StringSpec({
    val firstElement = mockk<XmlText>()

    afterTest {
        clearAllMocks()
    }

    fun createTextValueUnderTest(textElements: Array<XmlText>): XmlTag {
        val underTest = mockk<XmlTag>()
        val xmlTagValue = mockk<XmlTagValue>()
        every { underTest.value } returns xmlTagValue
        every { xmlTagValue.textElements } returns textElements

        return underTest
    }

    fun createGetChildUnderTest(vararg children: XmlTagChild) =
        mockk<XmlTag>().apply {
            every { this@apply.children } returns children
        }

    fun createXmlTagChild(name: String) =
        mockk<XmlTag>().apply {
            every { this@apply.name } returns name
        }

    "should get the first text if present" {
        table(
            headers("elements", "expected"),
            row(arrayOf<XmlText>(), null),
            row(arrayOf<XmlText>(firstElement), firstElement),
            row(arrayOf<XmlText>(firstElement, mockk()), firstElement)
        ).forAll { elements, expected ->
            val underTest = createTextValueUnderTest(elements)

            val actual = underTest.textValue

            actual shouldBe expected
        }
    }

    "should return null when node misses children" {
        val underTest = createGetChildUnderTest()

        val actual = underTest.getChildTag("test")

        actual shouldBe null
    }

    "should return null when children are not XmlTag" {
        val underTest = createGetChildUnderTest(mockk<XmlText>())

        val actual = underTest.getChildTag("test")

        actual shouldBe null
    }

    "should return null when node has children but not the searched one" {
        val underTest = createGetChildUnderTest(createXmlTagChild("a"), createXmlTagChild("b"))

        val actual = underTest.getChildTag("c")

        actual shouldBe null
    }

    "should return expected node" {
        val underTest = createGetChildUnderTest(createXmlTagChild("a"), createXmlTagChild("b"), mockk<XmlText>())

        val actual = underTest.getChildTag("b")

        actual shouldNotBe null
        actual!!.name shouldBe "b"
    }
})
