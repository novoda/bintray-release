package com.novoda.gradle.release

import com.jfrog.bintray.gradle.BintrayPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication

class ReleasePlugin implements Plugin<Project> {

    void apply(Project project) {
        PublishExtension extension = project.extensions.create('publish', PublishExtension, project)

        project.apply([plugin: 'maven-publish'])
        attachArtifacts(project, extension)

        new BintrayPlugin().apply(project)
        delayBintrayConfigurationUntilPublishExtensionIsEvaluated(project, extension)
    }

    void attachArtifacts(Project project, PublishExtension extension) {
        def projectArtifacts = project.plugins.hasPlugin('com.android.library') ? new AndroidArtifacts(project) : new JavaArtifacts(project)
        project.publishing {
            publications {
                maven(MavenPublication) {
                    groupId extension.groupId
                    artifactId extension.artifactId
                    version extension.publishVersion ?: extension.version
                    artifacts = projectArtifacts.all(it.name)
                    from projectArtifacts.components()
                }
            }
        }
    }

    private delayBintrayConfigurationUntilPublishExtensionIsEvaluated(Project project, PublishExtension extension) {
        project.afterEvaluate {
            new BintrayConfiguration(extension).configure(project)
        }
    }

}
