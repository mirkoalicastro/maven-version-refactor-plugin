package com.mirkoalicastro.mavenversionrefactor.domain

data class Dependency(val groupId: String, val artifactId: String, val version: Version) {
    fun getVersion() = version.value

    fun setVersion(value: String) {
        version.set(value)
    }
}
