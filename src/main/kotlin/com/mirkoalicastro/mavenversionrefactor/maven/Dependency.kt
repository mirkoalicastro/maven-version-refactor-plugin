package com.mirkoalicastro.mavenversionrefactor.maven

data class Dependency(val groupId: String, val artifactId: String, private val _version: Version) {
    var version: String
        get() = _version.value
        set(value) = _version.set(value)
}
