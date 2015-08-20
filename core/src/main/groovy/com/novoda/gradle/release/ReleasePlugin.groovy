package com.novoda.gradle.release

import com.jfrog.bintray.gradle.BintrayPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication

class ReleasePlugin implements Plugin<Project> {

    void apply(Project project) {
        PublishExtension extension = project.extensions.create('publish', PublishExtension)
        project.afterEvaluate {
            project.apply([plugin: 'maven-publish'])
            attachArtifacts(project)
            new BintrayPlugin().apply(project)
            new BintrayConfiguration(extension).configure(project)
        }
    }

    void attachArtifacts(Project project) {
        Artifacts artifacts = project.plugins.hasPlugin('com.android.library') ? new AndroidArtifacts() : new JavaArtifacts()
        PropertyFinder propertyFinder = new PropertyFinder(project, project.publish)
        project.publishing {
            publications {
                maven(MavenPublication) {
                    groupId project.publish.groupId
                    artifactId project.publish.artifactId
                    version propertyFinder.getPublishVersion()

                    artifacts.all(it.name, project).each {
                        delegate.artifact it
                    }

                    from artifacts.from(project)
                }
            }
        }
    }
}
