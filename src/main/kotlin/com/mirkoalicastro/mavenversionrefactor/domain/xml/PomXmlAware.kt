package com.mirkoalicastro.mavenversionrefactor.domain.xml

import com.intellij.psi.xml.XmlTag
import com.mirkoalicastro.mavenversionrefactor.domain.xml.XmlNodeName.PROPERTIES
import com.mirkoalicastro.mavenversionrefactor.xml.getChildTag
import org.apache.commons.lang3.StringUtils.EMPTY

data class PomXmlAware(val project: XmlTag, val dependencyXmlAware: DependencyXmlAware) {
    fun setVersion(version: String) = dependencyXmlAware.setVersion(version)

    fun getProperties() = project.getChildTag(PROPERTIES.xmlName)

    fun addProperties() = addProperty(project, PROPERTIES.xmlName)

    private fun addProperty(parent: XmlTag, node: String) {
        val childTag = parent.createChildTag(node, parent.namespace, EMPTY, true)
        parent.addSubTag(childTag, true)
    }
}
