package domain.constant

enum class XmlNodeName(val xmlName: String) {
    PROPERTIES("properties"), DEPENDENCY("dependency"), VERSION("version"),
    GROUP_ID("groupId"), ARTIFACT_ID("artifactId"), PROJECT("project")
}
