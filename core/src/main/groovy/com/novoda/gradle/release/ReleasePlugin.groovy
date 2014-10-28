package com.novoda.gradle.release

import org.gradle.BuildAdapter
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.invocation.Gradle

class ReleasePlugin implements Plugin<Project> {

    void apply(Project project) {
        project.apply([plugin: 'maven'])
        project.apply([plugin: 'com.jfrog.bintray'])

        project.uploadArchives.repositories.mavenDeployer {}

        attachExtension(project)
    }

    void attachExtension(Project project) {
        PublishExtension extension = project.extensions.create('publish', PublishExtension)
        def mavenDeployer = project.uploadArchives.repositories.mavenDeployer

        def projectAdapter = [
                projectsEvaluated: { Gradle gradle ->
                    if (!extension.localReleasePath) {
                        extension.localReleasePath = "${project.buildDir}/release"
                    }

                    mavenDeployer.with {
                        pom.groupId = extension.groupId
                        pom.artifactId = extension.artifactId
                        pom.version = extension.version

                        repository(url: "file://${extension.localReleasePath}")
                    }

                    attachArtifacts(project)

                    new BintrayConfiguration(extension).configure(project)
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
