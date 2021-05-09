package com.mirkoalicastro.mavenversionrefactor.xml

import com.intellij.psi.xml.XmlTag

fun XmlTag.getChildTag(name: String) = value.children
    .filterIsInstance<XmlTag>()
    .firstOrNull { name.equals(it.name, ignoreCase = true) }
