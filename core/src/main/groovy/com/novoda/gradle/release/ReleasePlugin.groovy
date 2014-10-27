package com.novoda.gradle.release

import org.gradle.BuildAdapter
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

class ReleasePlugin implements Plugin<Project> {

    def void apply(Project project) {
        project.apply([plugin: 'maven'])
        def extension = project.extensions.create('publish', PublishExtention)

        if (project.plugins.hasPlugin('com.android.library')) {
            applyForAndroid(project)
        } else {
            applyForTheRest(project)
        }

        def localReleaseDest = "${project.buildDir}/release"
        project.uploadArchives.repositories.mavenDeployer {
            repository(url: "file://${localReleaseDest}")
        }
        def mavenDeployer = project.uploadArchives.repositories.mavenDeployer

        def projectAdapter = [
                localReleaseDest : "${project.buildDir}/release",
                projectsEvaluated: { Gradle gradle ->
                    mavenDeployer.with {
                        pom.groupId = extension.groupId
                        pom.artifactId = extension.artifactId
                        pom.version = extension.version
                    }
                }
        ] as BuildAdapter
        project.gradle.addBuildListener(projectAdapter)
    }

    def applyForAndroid(Project project) {
        def androidJavadocs = project.task('androidJavadocs', type: Javadoc) {
            source = project.android.sourceSets.main.java.srcDirs
            classpath += project.files(project.android.getBootClasspath().join(File.pathSeparator))
        }

        def androidJavadocsJar = project.task('androidJavadocsJar', type: Jar, dependsOn: androidJavadocs) {
            classifier = 'javadoc'
            from androidJavadocs.destinationDir
        }

        def androidSourcesJar = project.task('androidSourcesJar', type: Jar) {
            classifier = 'sources'
            from project.android.sourceSets.main.java.srcDirs
        }

        project.artifacts {
            archives androidSourcesJar
            archives androidJavadocsJar
        }
    }

    def applyForTheRest(Project project) {
        def sourcesJar = project.task('sourcesJar', type: Jar) {
            classifier = 'sources'
            from project.sourceSets.main.allSource
        }

        def javadocJar = project.task('javadocJar', type: Jar) {
            classifier = 'javadoc'
            from project.javadoc.destinationDir
        }

        artifacts {
            archives sourcesJar
            archives javadocJar
        }
    }

}
