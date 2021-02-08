package com.mirkoalicastro.mavenversionrefactor.provider

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlTag
import com.mirkoalicastro.mavenversionrefactor.domain.constant.XmlNodeName.PROPERTIES

class PropertiesProvider {

    fun provide(project: XmlTag?) = project?.children?.extractProperties()

    private fun Array<PsiElement>.extractProperties() =
            filter { XmlTag::class.java.isInstance(it) }
                    .map { XmlTag::class.java.cast(it) }
                    .firstOrNull { PROPERTIES.xmlName == it.name.toLowerCase() }
}
