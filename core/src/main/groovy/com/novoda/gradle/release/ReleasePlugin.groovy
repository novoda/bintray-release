package com.novoda.gradle.release

import com.android.build.gradle.LibraryExtension
import com.jfrog.bintray.gradle.BintrayPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication

class ReleasePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        PublishExtension extension = project.extensions.create('publish', PublishExtension)
        project.afterEvaluate {
            extension.validate()
            project.apply([plugin: 'maven-publish'])
            attachArtifacts(extension, project)
            new BintrayPlugin().apply(project)
            new BintrayConfiguration(extension).configure(project)
        }
    }

    void attachArtifacts(PublishExtension extension, Project project) {
        if (project.plugins.hasPlugin('com.android.library')) {
            def libraryExtension = project.extensions.findByName("android") as LibraryExtension
            libraryExtension.libraryVariants.all {
                addArtifact(project, it.name, extension, new AndroidArtifacts(it))
            }
        } else {
            addArtifact(project, 'maven', extension, new JavaArtifacts())
        }
    }


    void addArtifact(Project project, String name, PublishExtension extension, Artifacts artifacts) {
        project.publishing.publications.create(name, MavenPublication) { MavenPublication publication ->
            publication.groupId = extension.groupId
            publication.artifactId = extension.artifactId
            publication.version = new PropertyFinder(project, extension).publishVersion

            artifacts.all(publication.name, project).each {
                delegate.artifact it
            }
            publication.from(artifacts.from(project))
        }
    }
}
