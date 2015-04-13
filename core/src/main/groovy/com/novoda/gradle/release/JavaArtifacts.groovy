package com.novoda.gradle.release
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar

class JavaArtifacts {

    private Project project

    JavaArtifacts(Project project) {
        this.project = project
    }

    def all(String publicationName) {
        [sourcesJar(publicationName), javadocJar(publicationName)]
    }

    def sourcesJar(String publicationName) {
        project.task(publicationName + 'SourcesJar', type: Jar) {
            classifier = 'sources'
            from project.sourceSets.main.allSource
        }
    }

    def javadocJar(String publicationName) {
        project.task(publicationName + 'JavadocJar', type: Jar) {
            classifier = 'javadoc'
            from project.javadoc.destinationDir
        }
    }

    def components() {
        project.components.java
    }

}
