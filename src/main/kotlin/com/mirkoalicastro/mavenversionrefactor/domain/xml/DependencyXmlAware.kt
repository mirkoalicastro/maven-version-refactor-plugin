package com.mirkoalicastro.mavenversionrefactor.domain.xml

import com.intellij.psi.xml.XmlText
import com.mirkoalicastro.mavenversionrefactor.domain.Dependency

data class DependencyXmlAware(val dependency: Dependency, val versionXmlText: XmlText) {
    fun setVersion(version: String) {
        versionXmlText.value = version
    }
}
