package com.novoda.gradle.release

import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar

class JavaArtifacts implements Artifacts {

    @Override
    def all(String publicationName, Project project) {
        [sourcesJar(publicationName, project), javadocJar(publicationName, project)]
    }

    private def sourcesJar(String publicationName, Project project) {
        project.task(publicationName + 'SourcesJar', type: Jar) {
            classifier = 'sources'
            from project.sourceSets.main.allSource
        }
    }

    private def javadocJar(String publicationName, Project project) {
        project.task(publicationName + 'JavadocJar', type: Jar) {
            classifier = 'javadoc'
            from project.javadoc.destinationDir
        }
    }
}
