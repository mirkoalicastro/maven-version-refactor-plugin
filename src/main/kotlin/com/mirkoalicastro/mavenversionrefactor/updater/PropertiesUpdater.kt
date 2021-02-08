package com.mirkoalicastro.mavenversionrefactor.updater

import com.intellij.psi.xml.XmlTag
import com.mirkoalicastro.mavenversionrefactor.domain.Pom
import com.mirkoalicastro.mavenversionrefactor.domain.constant.XmlNodeName
import org.apache.commons.lang3.StringUtils
import com.mirkoalicastro.mavenversionrefactor.provider.PropertiesProvider

class PropertiesUpdater(
        private val propertiesProvider: PropertiesProvider = PropertiesProvider()
) {
    fun addVersionToProperties(pom: Pom, freeVersion: String) {
        val properties = getPropertiesOrCreateIfMissing(pom.project)
        addProperty(properties, freeVersion, getVersion(pom), false)
    }

    private fun getPropertiesOrCreateIfMissing(project: XmlTag) =
            propertiesProvider.provide(project) ?: createProperties(project)

    private fun getVersion(pom: Pom) =
            pom.xmlDependency.dependency.version

    private fun createProperties(project: XmlTag): XmlTag {
        addProperty(project, XmlNodeName.PROPERTIES.xmlName, StringUtils.EMPTY, true)
        return propertiesProvider.provide(project)!!
    }

    private fun addProperty(parent: XmlTag, node: String, value: String, first: Boolean) {
        val childTag = parent.createChildTag(node, parent.namespace, value, true)
        parent.addSubTag(childTag, first)
    }
}
