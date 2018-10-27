package com.novoda.gradle.release

import com.jfrog.bintray.gradle.BintrayPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

class ReleasePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        PublishExtension extension = project.extensions.create('publish', PublishExtension)
        project.afterEvaluate {
            extension.validate()
            attachArtifacts(extension, project)
            new BintrayConfiguration(extension).configure(project)
        }
        project.apply([plugin: 'maven-publish'])
        new BintrayPlugin().apply(project)
    }

    private static void attachArtifacts(PublishExtension extension, Project project) {
        if (project.plugins.hasPlugin('com.android.library')) {
            project.android.libraryVariants.all { variant ->
                String publicationName = variant.name
                attachArtifacts(project, extension, publicationName, new AndroidArtifacts(variant))
            }
        } else {
            attachArtifacts(project, extension, 'maven', new JavaArtifacts())
        }
    }


    private static void attachArtifacts(Project project, PublishExtension extension, String publicationName, Artifacts artifacts) {
        PropertyFinder propertyFinder = new PropertyFinder(project, extension)
        String groupId = extension.groupId
        String artifactId = extension.artifactId
        String version = propertyFinder.publishVersion

        def artifactSources = artifacts.all(publicationName, project)
        SoftwareComponent softwareComponent = artifacts.from(project)

        PublicationContainer publicationContainer = project.extensions.getByType(PublishingExtension).publications
        publicationContainer.create(publicationName, MavenPublication) { MavenPublication publication ->
            publication.groupId = groupId
            publication.artifactId = artifactId
            publication.version = version
            artifactSources.each { publication.artifact it }
            publication.from softwareComponent
        }
    }
}
