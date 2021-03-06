package com.mirkoalicastro.mavenversionrefactor.maven

import com.intellij.psi.xml.XmlText

data class Version(val value: String, val tag: XmlText) {
    fun set(version: String) {
        tag.value = version
    }
}
