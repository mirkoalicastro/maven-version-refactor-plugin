package com.mirkoalicastro.mavenversionrefactor.updater

import com.intellij.psi.xml.XmlTag
import com.mirkoalicastro.mavenversionrefactor.domain.xml.PomXmlAware

class PropertiesUpdater {
    fun addVersion(pom: PomXmlAware, freeVersion: String) {
        val properties = getPropertiesOrCreateIfMissing(pom)
        addProperty(properties, freeVersion, getVersion(pom))
        pom.setVersion("\${$freeVersion}")
    }

    private fun getPropertiesOrCreateIfMissing(pom: PomXmlAware) =
        pom.getProperties() ?: run {
            pom.addProperties()
            pom.getProperties()!!
        }

    private fun getVersion(pom: PomXmlAware) = pom.dependencyXmlAware.dependency.version

    private fun addProperty(parent: XmlTag, node: String, value: String) {
        val property = parent.createChildTag(node, parent.namespace, value, true)
        parent.addSubTag(property, false)
    }
}
