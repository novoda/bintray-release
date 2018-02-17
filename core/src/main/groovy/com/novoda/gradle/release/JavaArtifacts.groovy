package com.novoda.gradle.release

import org.gradle.api.Project
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.tasks.bundling.Jar

class JavaArtifacts implements Artifacts {

    @Override
    def all(String publicationName, Project project) {
        [sourcesJar(publicationName, project), javadocJar(publicationName, project)]
    }

    def sourcesJar(String publicationName, Project project) {
        project.tasks.create(publicationName + 'SourcesJar', Jar) {
            it.classifier = 'sources'
            from project.sourceSets.main.allSource
        }
    }

    def javadocJar(String publicationName, Project project) {
        project.tasks.create(publicationName + 'JavadocJar', Jar) {
            it.classifier = 'javadoc'
            from project.javadoc.destinationDir
        }
    }

    @Override
    SoftwareComponent from(Project project) {
        project.components.java
    }

}
