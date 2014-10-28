package com.novoda.gradle.release

import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar;

class JavaArtifacts implements Artifacts {

    void attachTo(Project project) {
        def sourcesJar = project.task('sourcesJar', type: Jar) {
            classifier = 'sources'
            from project.sourceSets.main.allSource
        }

        def javadocJar = project.task('javadocJar', type: Jar) {
            classifier = 'javadoc'
            from project.javadoc.destinationDir
        }

        project.artifacts {
            archives sourcesJar
            archives javadocJar
        }
    }
}
