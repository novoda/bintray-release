package com.novoda.gradle.release

import com.jfrog.bintray.gradle.BintrayPlugin
import guru.stefma.androidartifacts.AndroidArtifactsExtension
import guru.stefma.androidartifacts.AndroidArtifactsPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication

class ReleasePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        PublishExtension extension = project.extensions.create('publish', PublishExtension)

        project.afterEvaluate {
            extension.validate()
            attachArtifacts(extension, project)
            new BintrayPlugin().apply(project)
            new BintrayConfiguration(extension).configure(project)
        }
    }

    void attachArtifacts(PublishExtension extension, Project project) {
        if (project.plugins.hasPlugin('com.android.library')) {
            project.plugins.apply(AndroidArtifactsPlugin.class)
            def artifactsExtension = project.extensions.getByType(AndroidArtifactsExtension.class)
            artifactsExtension.artifactId = extension.artifactId
        } else {
            project.apply([plugin: 'maven-publish'])
            addArtifact(project, 'maven', project.publish.artifactId, new JavaArtifacts())
        }
    }

    void addArtifact(Project project, String name, String artifact, JavaArtifacts artifacts) {
        PropertyFinder propertyFinder = new PropertyFinder(project, project.publish)
        project.publishing.publications.create(name, MavenPublication) {
            groupId project.publish.groupId
            artifactId artifact
            version = propertyFinder.publishVersion

            artifacts.all(it.name, project).each {
                delegate.artifact it
            }
            from artifacts.from(project)
        }
    }
}
