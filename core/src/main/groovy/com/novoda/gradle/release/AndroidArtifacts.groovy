package com.novoda.gradle.release

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

class AndroidArtifacts implements Artifacts {

    def variant

    AndroidArtifacts(variant) {
        this.variant = variant
    }

    @Override
    def all(String publicationName, Project project) {
        [sourcesJar(project), javadocJar(project), mainJar(project)]
    }

    def sourcesJar(Project project) {
        project.tasks.create("${variant.name}AndroidSourcesJar", Jar) { task ->
            task.classifier = 'sources'
            variant.sourceSets.each { sourceSet ->
                from sourceSet.java.srcDirs
            }
        }
    }

    def javadocJar(Project project) {
        Task androidJavadocs = project.tasks.create("${variant.name}AndroidJavadocs", Javadoc) { task ->
            variant.sourceSets.each {
                delegate.source it.java.srcDirs
            }
            task.classpath += project.files(project.android.getBootClasspath().join(File.pathSeparator))
            task.classpath += variant.javaCompile.classpath
            task.classpath += variant.javaCompile.outputs.files
        }

        project.tasks.create("${variant.name}AndroidJavadocsJar", Jar) { task ->
            task.dependsOn(androidJavadocs)
            task.classifier = 'javadoc'
            from androidJavadocs.destinationDir
        }
    }

    def mainJar(Project project) {
        def archiveBaseName = project.hasProperty("archivesBaseName") ? project.getProperty("archivesBaseName") : project.name
        "$project.buildDir/outputs/aar/$archiveBaseName-${variant.baseName}.aar"
    }

    @Override
    SoftwareComponent from(Project project) {
        project.components.add(new AndroidLibrary(project))
        project.components.android
    }

}
