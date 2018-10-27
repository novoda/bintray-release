package com.novoda.gradle.release.internal

import com.novoda.gradle.release.MavenPublicationAttachments
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.JavaCompile
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
        JavaCompile javaCompile = project.compileJava
        return project.task("genereateSourcesJarFor${publicationName.capitalize()}Publication", type: Jar) { Jar jar ->
            jar.classifier = 'sources'
            jar.from javaCompile.source
        }
    }

    private Task publicationJavadocJar() {
        Javadoc javadoc = project.javadoc
        return project.task("genereateJavadocsJarFor${publicationName.capitalize()}Publication", type: Jar) { Jar jar ->
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
