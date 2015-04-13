package com.novoda.gradle.release

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

class AndroidArtifacts {

    private Project project

    AndroidArtifacts(Project project) {
        this.project = project
    }

    def all(String publicationName) {
        [sourcesJar(publicationName), javadocJar(publicationName), mainJar()]
    }

    def sourcesJar(String publicationName) {
        project.task(publicationName + 'AndroidSourcesJar', type: Jar) {
            classifier = 'sources'
            from project.android.sourceSets.main.java.srcDirs
        }
    }

    def javadocJar(String publicationName) {
        Task androidJavadocs = project.task(publicationName + 'AndroidJavadocs', type: Javadoc) {
            source = project.android.sourceSets.main.java.srcDirs
            classpath += project.android.bootClasspath
            classpath += project.android.libraryVariants.toList().first().javaCompile.classpath
            classpath += project.android.libraryVariants.toList().first().javaCompile.outputs.files
        }

        project.task(publicationName + 'AndroidJavadocsJar', type: Jar, dependsOn: androidJavadocs) {
            classifier = 'javadoc'
            from androidJavadocs
        }
    }

    def mainJar() {
        "$project.buildDir/outputs/aar/$project.name-release.aar" // TODO How can we improve this?
    }

    def components() {
        project.components.add(AndroidLibrary.newInstance(project))
        project.components.android
    }

}
