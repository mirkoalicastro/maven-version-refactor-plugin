package domain

import com.intellij.psi.xml.XmlText

data class XmlDependency(val dependency: Dependency, val versionXmlText: XmlText)