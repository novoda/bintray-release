package com.novoda.gradle.release

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication

class ReleasePlugin implements Plugin<Project> {

    void apply(Project project) {
        PublishExtension extension = project.extensions.create('publish', PublishExtension)

        project.apply([plugin: 'maven-publish'])
        project.apply([plugin: 'com.jfrog.bintray'])

        project.afterEvaluate {
            new BintrayConfiguration(extension).configure(project)
        }

        attachArtifacts(project)
    }

    void attachArtifacts(Project project) {
        Artifacts artifacts = project.plugins.hasPlugin('com.android.library') ? new AndroidArtifacts() : new JavaArtifacts()
        project.publishing {
            publications {
                maven(MavenPublication) {
                    groupId project.publish.groupId
                    artifactId project.publish.artifactId
                    version project.publish.version

                    artifact artifacts.mainJar(project)
                    artifact artifacts.sourcesJar(project)
                    artifact artifacts.javadocJar(project)
                }
            }
        }
    }

}
