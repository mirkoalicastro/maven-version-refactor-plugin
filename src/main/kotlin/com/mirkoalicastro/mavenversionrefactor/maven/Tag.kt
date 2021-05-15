package com.mirkoalicastro.mavenversionrefactor.maven

enum class Tag(val value: String) {
    Properties("properties"),
    Dependency("dependency"),
    Plugin("plugin"),
    Version("version"),
    GroupId("groupId"),
    ArtifactId("artifactId"),
    Project("project")
}
