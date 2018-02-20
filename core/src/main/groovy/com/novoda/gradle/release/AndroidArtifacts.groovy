package com.novoda.gradle.release

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

class AndroidArtifacts implements Artifacts {

    def variant

    // TODO: Declare that variant is
    // https://google.github.io/android-gradle-dsl/current/com.android.build.gradle.LibraryExtension.html#com.android.build.gradle.LibraryExtension:libraryVariants
    AndroidArtifacts(variant) {
        this.variant = variant
    }

    @Override
    def all(String publicationName, Project project) {
        [sourcesJar(project), javadocJar(project), mainJar(project)]
    }

    def sourcesJar(Project project) {
        project.tasks.create("${variant.name}AndroidSourcesJar", Jar) {
            it.classifier = 'sources'
            variant.sourceSets.each {
                from it.java.srcDirs
            }
        }
    }

    def javadocJar(Project project) {
        Task androidJavadocs = project.tasks.create("${variant.name}AndroidJavadocs", Javadoc) {
            variant.sourceSets.each {
                delegate.source it.java.srcDirs
            }
            it.classpath += project.files(project.android.getBootClasspath().join(File.pathSeparator))
            it.classpath += variant.javaCompile.classpath
            it.classpath += variant.javaCompile.outputs.files
        }

        project.tasks.create("${variant.name}AndroidJavadocsJar", Jar) {
            it.dependsOn(androidJavadocs)
            it.classifier = 'javadoc'
            from androidJavadocs.destinationDir
        }
    }

    def mainJar(Project project) {
        "$project.buildDir/outputs/aar/${project.name}-${variant.baseName}.aar"
    }

    @Override
    SoftwareComponent from(Project project) {
        project.components.add(new AndroidLibrary(project))
        project.components.android
    }

}
