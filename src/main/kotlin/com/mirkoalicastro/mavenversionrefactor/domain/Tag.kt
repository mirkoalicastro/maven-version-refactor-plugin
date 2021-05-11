package com.mirkoalicastro.mavenversionrefactor.domain

enum class Tag(val xmlName: String) {
    Properties("properties"),
    Dependency("dependency"),
    Plugin("plugin"),
    Version("version"),
    GroupId("groupId"),
    ArtifactId("artifactId"),
    Project("project")
}
