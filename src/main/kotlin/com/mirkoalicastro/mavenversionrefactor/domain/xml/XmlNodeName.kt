package com.mirkoalicastro.mavenversionrefactor.domain.xml

enum class XmlNodeName(val xmlName: String) {
    PROPERTIES("properties"),
    DEPENDENCY("dependency"),
    PLUGIN("plugin"),
    VERSION("version"),
    GROUP_ID("groupId"),
    ARTIFACT_ID("artifactId"),
    PROJECT("project")
}
