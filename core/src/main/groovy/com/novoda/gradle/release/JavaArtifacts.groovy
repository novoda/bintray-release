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

    def mainJar(Project project) {
        "$project.buildDir/libs/$project.name-${project.version}.jar" // TODO How can we improve this?
    }
}
