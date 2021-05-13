package com.mirkoalicastro.mavenversionrefactor.maven

import com.intellij.psi.xml.XmlTag
import com.mirkoalicastro.mavenversionrefactor.xml.getChildTag

data class Pom(
    val project: XmlTag,
    val dependency: Dependency
) {
    fun addVersion(name: String) {
        addProperty(getProperties(), name, dependency.version)
        dependency.version = "\${$name}"
    }

    private fun getProperties() =
        getCurrentProperties() ?: run {
            addProperty(project, Tag.Properties.xmlName)
            getCurrentProperties()!!
        }

    private fun getCurrentProperties() = project.getChildTag(Tag.Properties.xmlName)

    private fun addProperty(parent: XmlTag, name: String, value: String = "") {
        val childTag = parent.createChildTag(name, parent.namespace, value, true)
        parent.addSubTag(childTag, true)
    }
}
