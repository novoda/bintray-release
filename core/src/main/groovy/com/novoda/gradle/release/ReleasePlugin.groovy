package com.novoda.gradle.release

import com.jfrog.bintray.gradle.BintrayPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication

class ReleasePlugin implements Plugin<Project> {

    void apply(Project project) {
        PublishExtension extension = project.extensions.create('publish', PublishExtension)

        project.apply([plugin: 'maven-publish'])
        attachArtifacts(project)

        new BintrayPlugin().apply(project)
        delayBintrayConfigurationUntilPublishExtensionIsEvaluated(project, extension)
    }

    void attachArtifacts(Project project) {
        project.publishing {
            publications {
                Artifacts artifacts = project.plugins.hasPlugin('com.android.library') ? new AndroidArtifacts() : new JavaArtifacts()

                maven(MavenPublication) {

                    groupId project.publish.groupId
                    artifactId project.publish.artifactId
                    version getProjectProperty(project, 'version', project.publish.version)

                    artifacts.all(it.name, project).each {
                        delegate.artifact it
                    }

                    from artifacts.from(project)
                }
            }
        }
    }

    private delayBintrayConfigurationUntilPublishExtensionIsEvaluated(Project project, extension) {
        project.afterEvaluate {
            new BintrayConfiguration(extension).configure(project)
        }
    }

    private String getProjectProperty(Project project, String propertyName, String defaultValue) {
        if (isPropertySet(project, propertyName)) {
            return project.getProperty(propertyName)
        } else {
            return defaultValue
        }
    }

    private boolean isPropertySet(Project project, String propertyName) {
        project.hasProperty(propertyName) && project.getProperty(propertyName) != 'unspecified'
    }

}
