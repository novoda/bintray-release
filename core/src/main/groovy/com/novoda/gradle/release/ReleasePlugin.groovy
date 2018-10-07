package com.novoda.gradle.release

import com.jfrog.bintray.gradle.BintrayPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ExcludeRule
import org.gradle.api.artifacts.ModuleDependency
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

    private void attachArtifacts(PublishExtension extension, Project project) {
        if (project.plugins.hasPlugin('com.android.library')) {
            project.android.libraryVariants.all { variant ->
                MavenPublication publication = createPublication(variant.name, project, extension, new AndroidArtifacts(variant))
                Set<ModuleDependency> dependenciesSet = new HashSet<>()
                ['compile', 'implementation', 'api'].each { configurationName ->
                    project.configurations.findByName(configurationName)?.with { configuration ->
                        dependenciesSet.addAll(configuration.incoming.dependencies.withType(ModuleDependency))
                    }
                }

                publication.pom.withXml {
                    def dependenciesNode = asNode().appendNode('dependencies')
                    dependenciesSet.each { ModuleDependency dependency ->
                        if (dependency.name != 'unspecified') {
                            def dependencyNode = dependenciesNode.appendNode('dependency')
                            dependencyNode.appendNode('groupId', dependency.group)
                            dependencyNode.appendNode('artifactId', dependency.name)
                            dependencyNode.appendNode('version', dependency.version)

                            if (!dependency.excludeRules.isEmpty()) {
                                def exclusions = dependencyNode.appendNode('exclusions')
                                dependency.excludeRules.each { ExcludeRule excludeRule ->
                                    def exclusion = exclusions.appendNode('exclusion')
                                    exclusion.appendNode('groupId', excludeRule.group)
                                    exclusion.appendNode('artifactId', excludeRule.module)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            MavenPublication publication = createPublication('maven', project, extension, new JavaArtifacts())
            publication.from project.components.java
        }
    }

    private MavenPublication createPublication(String name, Project project, PublishExtension extension, Artifacts artifacts) {
        PublishingExtension mavenPublishingExtension = project.extensions.findByType(PublishingExtension)
        PropertyFinder propertyFinder = new PropertyFinder(project, extension)
        MavenPublication publication = mavenPublishingExtension.publications.create(name, MavenPublication) {
            groupId = extension.groupId
            artifactId = extension.artifactId
            version = propertyFinder.publishVersion
        }
        artifacts.all(publication.name, project).each { artifactSource ->
            publication.artifact artifactSource
        }
        return publication
    }

}
