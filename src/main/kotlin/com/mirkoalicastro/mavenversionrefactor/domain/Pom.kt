package com.mirkoalicastro.mavenversionrefactor.domain

import com.intellij.psi.xml.XmlTag
import com.mirkoalicastro.mavenversionrefactor.xml.getChildTag

data class Pom(
    val project: XmlTag,
    val dependency: Dependency
) {
    fun addVersion(name: String) {
        addProperty(getProperties(), name, dependency.getVersion())
        dependency.setVersion("\${$name}")
    }

    private fun getProperties() =
        getCurrentProperties() ?: run {
            addProperty(project, Tag.Properties.xmlName)
            getCurrentProperties()!!
        }

    private fun getCurrentProperties() = project.getChildTag(Tag.Properties.xmlName)

    private fun addProperty(parent: XmlTag, node: String, value: String = "") {
        val childTag = parent.createChildTag(node, parent.namespace, value, true)
        parent.addSubTag(childTag, true)
    }
}
