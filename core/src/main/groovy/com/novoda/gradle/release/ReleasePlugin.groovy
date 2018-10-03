package com.novoda.gradle.release

import com.jfrog.bintray.gradle.BintrayPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
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
        PublishingExtension mavenPublishingExtension = project.extensions.findByType(PublishingExtension)

        PropertyFinder propertyFinder = new PropertyFinder(project, extension)
        if (project.plugins.hasPlugin('com.android.library')) {
            project.android.libraryVariants.all { variant ->
                def name = variant.name
                Artifacts artifacts = new AndroidArtifacts(variant)
                MavenPublication mavenPublication = mavenPublishingExtension.publications.create(name, MavenPublication) {
                    groupId extension.groupId
                    artifactId extension.artifactId
                    version = propertyFinder.publishVersion

                    artifacts.all(name, project).each {
                        artifact it
                    }

                    pom.withXml {
                        def dependenciesNode = asNode().appendNode('dependencies')
                        def configurationContainer = project.configurations
                        def allDependencies = []
                        allDependencies.addAll(configurationContainer.findByName('compile')?.allDependencies ?: [])
                        allDependencies.addAll(configurationContainer.findByName('implementation')?.allDependencies ?: [])
                        allDependencies.addAll(configurationContainer.findByName('api')?.allDependencies ?: [])

                        allDependencies.each {
                            if (it.name != 'unspecified') {
                                def dependencyNode = dependenciesNode.appendNode('dependency')
                                dependencyNode.appendNode('groupId', it.group)
                                dependencyNode.appendNode('artifactId', it.name)
                                dependencyNode.appendNode('version', it.version)
                            }
                        }
                    }
                }
            }
        } else {
            Artifacts artifacts = new JavaArtifacts()
            def name = 'maven'
            MavenPublication mavenPublication = mavenPublishingExtension.publications.create(name, MavenPublication) {
                groupId extension.groupId
                artifactId extension.artifactId
                version = propertyFinder.publishVersion

                artifacts.all(name, project).each {
                    artifact it
                }
                from project.components.java
            }
        }
    }
}
