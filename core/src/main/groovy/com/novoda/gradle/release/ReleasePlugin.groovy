package com.novoda.gradle.release

import com.jfrog.bintray.gradle.BintrayPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication

class ReleasePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        PublishExtension extension = project.extensions.create('publish', PublishExtension)
        project.afterEvaluate {
            checkClosureSetup(extension)
            project.apply([plugin: 'maven-publish'])
            attachArtifacts(extension, project)
            new BintrayPlugin().apply(project)
            new BintrayConfiguration(extension).configure(project)
        }
    }

    /**
     * Give the user quicker and more obvious feedback when they
     * haven't set their project up correctly
     */
    private static void checkClosureSetup(PublishExtension extension) {
        String extensionError = "";
        if (extension.userOrg == null) {
            extensionError += "Missing userOrg. "
        }
        if (extension.groupId == null) {
            extensionError += "Missing groupId. "
        }
        if (extension.artifactId == null) {
            extensionError += "Missing artifactId. "
        }
        if (extension.publishVersion == null) {
            extensionError += "Missing publishVersion. "
        }
        if (extension.desc == null) {
            extensionError += "Missing desc. "
        }
        if (extensionError) {
            String prefix = "Have you created the publish closure? "
            throw new IllegalStateException(prefix + extensionError)
        }
    }

    void attachArtifacts(PublishExtension extension, Project project) {
        if (project.plugins.hasPlugin('com.android.library')) {
            project.android.libraryVariants.all { variant ->
                addArtifact(project, variant.name as String, extension, new AndroidArtifacts(variant))
            }
        } else {
            addArtifact(project, 'maven', extension, new JavaArtifacts())
        }
    }


    void addArtifact(Project project, String name, PublishExtension extension, Artifacts artifacts) {
        project.publishing.publications.create(name, MavenPublication) {
            def publication = it as MavenPublication
            publication.groupId = extension.groupId
            publication.artifactId = extension.artifactId
            publication.version = new PropertyFinder(project, extension).publishVersion

            artifacts.all(it.name, project).each {
                delegate.artifact it
            }
            publication.from(artifacts.from(project))
        }
    }
}
