package com.novoda.gradle.release

import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

class AndroidArtifacts implements Artifacts {

    def all(String publicationName, Project project) {
        [sourcesJar(publicationName, project), javadocJar(publicationName, project), mainJar(project)]
    }

    def sourcesJar(String publicationName, Project project) {
        project.task(publicationName + 'AndroidSourcesJar', type: Jar) {
            classifier = 'sources'
            from project.android.sourceSets.main.java.srcDirs
        }
    }

    def javadocJar(String publicationName, Project project) {
        def androidJavadocs = project.task(publicationName + 'AndroidJavadocs', type: Javadoc) {
            source = project.android.sourceSets.main.java.srcDirs
            classpath += project.files(project.android.getBootClasspath().join(File.pathSeparator))
            classpath += project.android.libraryVariants.toList().first().javaCompile.classpath
            classpath += project.android.libraryVariants.toList().first().javaCompile.outputs.files
        }

        project.task(publicationName + 'AndroidJavadocsJar', type: Jar, dependsOn: androidJavadocs) {
            classifier = 'javadoc'
            from androidJavadocs.destinationDir
        }
    }

    def mainJar(Project project) {
        "$project.buildDir/outputs/aar/$project.name-release.aar" // TODO How can we improve this?
    }

    def from(Project project) {
        project.components.add(AndroidLibrary.newInstance(project))
        project.components.android
    }

}
