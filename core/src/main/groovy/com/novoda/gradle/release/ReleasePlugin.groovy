package com.novoda.gradle.release

import org.gradle.BuildAdapter
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.invocation.Gradle

class ReleasePlugin implements Plugin<Project> {

    void apply(Project project) {
        project.apply([plugin: 'maven'])
        project.apply([plugin: 'com.jfrog.bintray'])

        def localReleaseDest = "${project.buildDir}/release"
        project.uploadArchives.repositories.mavenDeployer {
            repository(url: "file://${localReleaseDest}")
        }

        attachExtension(project)
    }

    void attachExtension(Project project) {
        def extension = project.extensions.create('publish', PublishExtention)
        def mavenDeployer = project.uploadArchives.repositories.mavenDeployer

        def projectAdapter = [
                projectsEvaluated: { Gradle gradle ->
                    mavenDeployer.with {
                        pom.groupId = extension.groupId
                        pom.artifactId = extension.artifactId
                        pom.version = extension.version
                    }

                    attachArtifacts(project)
                }
        ] as BuildAdapter
        project.gradle.addBuildListener(projectAdapter)
    }


    void attachArtifacts(Project project) {
        Artifacts artifacts
        if (project.plugins.hasPlugin('com.android.library')) {
            artifacts = new AndroidArtifacts()
        } else {
            artifacts = new JavaArtifacts()
        }
        artifacts.attachTo(project)
    }

}
