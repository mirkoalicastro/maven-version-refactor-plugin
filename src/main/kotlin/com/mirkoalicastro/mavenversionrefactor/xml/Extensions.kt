package com.mirkoalicastro.mavenversionrefactor.xml

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlTag

fun XmlTag.getTags() = value.children.filterIsInstance<XmlTag>()

fun List<XmlTag>.getTag(name: String) = firstOrNull { it.name == name }

tailrec fun PsiElement.findBack(vararg nodes: String): XmlTag? =
    when {
        parent == null -> null
        (parent as? XmlTag)?.name in nodes -> parent as XmlTag
        else -> parent.findBack(*nodes)
    }
