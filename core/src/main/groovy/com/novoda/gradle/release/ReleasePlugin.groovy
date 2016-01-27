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
        if (project.plugins.hasPlugin('com.android.library')) {
            project.android.libraryVariants.each { variant ->
                if (!variant.buildType.debuggable) {
                    def artifactId = project.name;
                    if (variant.productFlavors.size() > 0) {
                        artifactId += '-' + variant.productFlavors.collect { it.name }.join("-")
                    }
                    addArtifact(project, variant.name, artifactId, new AndroidArtifacts());
                }
            }
        } else {
            addArtifact(project, 'maven', project.publish.artifactId, new JavaArtifacts())
        }
    }


    void addArtifact(Project project, String name, String artifact, Artifacts artifacts) {
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
