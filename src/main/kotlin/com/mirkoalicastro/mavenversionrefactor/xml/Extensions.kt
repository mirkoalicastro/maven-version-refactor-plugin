package com.mirkoalicastro.mavenversionrefactor.xml

import com.intellij.psi.xml.XmlTag

fun XmlTag.getTags() = value.children.filterIsInstance<XmlTag>()

fun List<XmlTag>.getTag(name: String) = firstOrNull { it.name == name }
