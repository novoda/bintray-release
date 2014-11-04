package com.novoda.gradle.release

import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

class AndroidArtifacts implements Artifacts {

    def sourcesJar(Project project) {
        project.task('androidSourcesJar', type: Jar) {
            classifier = 'sources'
            from project.android.sourceSets.main.java.srcDirs
        }
    }

    def javadocJar(Project project) {
        def androidJavadocs = project.task('androidJavadocs', type: Javadoc) {
            source = project.android.sourceSets.main.java.srcDirs
            classpath += project.files(project.android.getBootClasspath().join(File.pathSeparator))
        }

        project.task('androidJavadocsJar', type: Jar, dependsOn: androidJavadocs) {
            classifier = 'javadoc'
            from androidJavadocs.destinationDir
        }
    }

    def mainJar(Project project) {
        "$project.buildDir/outputs/aar/$project.name-release.aar" // TODO How can we improve this?
    }

    def from(Project project) {
        def configuration = project.configurations.getAll().find { it.dependencies }
        project.components.add(new AndroidLibrary(configuration.dependencies))
        project.components.android
    }

    def all(Project project) {
        [sourcesJar(project), javadocJar(project), mainJar(project)]
    }
}
