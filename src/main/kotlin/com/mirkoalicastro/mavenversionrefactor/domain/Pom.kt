package com.mirkoalicastro.mavenversionrefactor.domain

import com.intellij.psi.xml.XmlTag

data class Pom(val project: XmlTag, val xmlDependency: XmlDependency)
