package com.novoda.gradle.release

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

class JavaAttachments extends MavenPublicationAttachments {

    private final String publicationName
    private final Project project
    private final List<Object> allArtifactSources

    JavaAttachments(String publicationName, Project project) {
        this.publicationName = publicationName
        this.project = project
        this.allArtifactSources = Arrays.asList(publicationSourcesJar(), publicationJavadocJar()).asImmutable()
    }

    private Task publicationSourcesJar() {
        SourceSetContainer sourceSets = project.sourceSets
        return project.task("${publicationName}SourcesJar", type: Jar) { Jar jar ->
            jar.classifier = 'sources'
            jar.from sourceSets.getByName('main').allJava.flatten()
        }
    }

    private Task publicationJavadocJar() {
        Javadoc javadoc = project.javadoc
        return project.task("${publicationName}JavadocJar", type: Jar) { Jar jar ->
            jar.classifier = 'javadoc'
            jar.from project.files(javadoc)
        }
    }

    @Override
    List<Object> getAllArtifactSources() {
        return allArtifactSources
    }

    @Override
    SoftwareComponent getSoftwareComponent() {
        return project.components.getByName('java')
    }
}
