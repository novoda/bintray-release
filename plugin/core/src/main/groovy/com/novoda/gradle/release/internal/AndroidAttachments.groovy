package com.novoda.gradle.release.internal

import com.novoda.gradle.release.MavenPublicationAttachments
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

class AndroidAttachments extends MavenPublicationAttachments {

    private final String publicationName
    private final Project project
    private final def variant
    private final List<Object> allArtifactSources

    AndroidAttachments(String publicationName, Project project, def variant) {
        this.publicationName = publicationName
        this.variant = variant
        this.project = project
        this.allArtifactSources = Arrays.asList(publicationSourcesJar(), publicationJavadocJar(), archivePath()).asImmutable()
    }

    private Task publicationSourcesJar() {
        project.task("genereateSourcesJarFor${publicationName.capitalize()}Publication", type: Jar) { Jar jar ->
            jar.classifier = 'sources'
            jar.from variant.sourceSets.collect { it.javaDirectories }.flatten()
        }
    }

    private Task publicationJavadocJar() {
        Javadoc javadoc = project.task("javadoc${variant.name.capitalize()}", type: Javadoc) { Javadoc javadoc ->
            javadoc.source = variant.javaCompiler.source
            javadoc.classpath = variant.javaCompiler.classpath
        } as Javadoc
        return project.task("genereateJavadocsJarFor${publicationName.capitalize()}Publication", type: Jar) { Jar jar ->
            jar.classifier = 'javadoc'
            jar.from project.files(javadoc)
        }
    }

    private def archivePath() {
        return variant.packageLibrary.archivePath
    }

    @Override
    List<Object> getAllArtifactSources() {
        return allArtifactSources
    }

    @Override
    SoftwareComponent getSoftwareComponent() {
        return new AndroidSoftwareComponent(project.objects, project.configurations)
    }
}
