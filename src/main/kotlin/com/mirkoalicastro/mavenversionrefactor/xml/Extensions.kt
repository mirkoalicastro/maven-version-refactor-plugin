package com.mirkoalicastro.mavenversionrefactor.xml

import com.intellij.psi.xml.XmlTag

fun XmlTag.getChildTag(name: String) = children
    .filterIsInstance<XmlTag>()
    .firstOrNull { name.equals(it.name, ignoreCase = true) }

val XmlTag.textValue get() = value.textElements.getOrNull(0)
