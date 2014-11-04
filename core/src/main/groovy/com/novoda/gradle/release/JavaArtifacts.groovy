package com.novoda.gradle.release

import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar

class JavaArtifacts implements Artifacts {

    def sourcesJar(Project project) {
        project.task('sourcesJar', type: Jar) {
            classifier = 'sources'
            from project.sourceSets.main.allSource
        }
    }

    def javadocJar(Project project) {
        project.task('javadocJar', type: Jar) {
            classifier = 'javadoc'
            from project.javadoc.destinationDir
        }
    }

    def from(Project project) {
        project.components.java
    }


    def all(Project project) {
        [sourcesJar(project), javadocJar(project)]
    }
}
