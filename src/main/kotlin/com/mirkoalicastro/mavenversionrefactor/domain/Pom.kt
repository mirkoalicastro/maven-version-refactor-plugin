package com.mirkoalicastro.mavenversionrefactor.domain

import com.intellij.psi.xml.XmlTag

data class Pom(val namespace: String, val project: XmlTag, val xmlDependency: XmlDependency) {
    companion object {
        private const val MAVEN_NAMESPACE = "^(http://maven\\.apache\\.org).*$"
    }

    fun hasValidNamespace() = namespace.matches(Regex(MAVEN_NAMESPACE))

}
