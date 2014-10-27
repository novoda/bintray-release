package com.novoda.gradle.release

import org.gradle.BuildAdapter
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.invocation.Gradle

class ReleasePlugin implements Plugin<Project> {

    void apply(Project project) {
        attachArtifacts(project)

        project.apply([plugin: 'maven'])
        def localReleaseDest = "${project.buildDir}/release"
        project.uploadArchives.repositories.mavenDeployer {
            repository(url: "file://${localReleaseDest}")
        }

        attachExtension(project)
    }

    void attachArtifacts(Project project) {
        ArtifactAttacher artifactAttacher
        if (project.plugins.hasPlugin('com.android.library')) {
            artifactAttacher = new AndroidArtifactAttacher()
        } else {
            artifactAttacher = new JavaArtifactAttacher()
        }
        artifactAttacher.attachTo(project)
    }

    void attachExtension(project) {
        def extension = project.extensions.create('publish', PublishExtention)
        def mavenDeployer = project.uploadArchives.repositories.mavenDeployer

        def projectAdapter = [
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

}
