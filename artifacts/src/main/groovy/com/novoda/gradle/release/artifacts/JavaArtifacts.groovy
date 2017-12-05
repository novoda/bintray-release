package com.novoda.gradle.release.artifacts

import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar

class JavaArtifacts implements Artifacts {

    def all(String publicationName, Project project) {
        [sourcesJar(publicationName, project), javadocJar(publicationName, project)]
    }

    def sourcesJar(String publicationName, Project project) {
        project.task(publicationName + 'SourcesJar', type: Jar) {
            classifier = 'sources'
            from project.sourceSets.main.allSource
        }
    }

    def javadocJar(String publicationName, Project project) {
        project.task(publicationName + 'JavadocJar', type: Jar) {
            classifier = 'javadoc'
            from project.javadoc.destinationDir
        }
    }

    def from(Project project) {
        project.components.java
    }

}
