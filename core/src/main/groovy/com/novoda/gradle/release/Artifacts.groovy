package com.novoda.gradle.release;

import org.gradle.api.Project

interface Artifacts {
    def sourcesJar(Project project)

    def javadocJar(Project project)

    def mainJar(Project project)
}
