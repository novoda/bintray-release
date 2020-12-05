package com.novoda.gradle.release.internal

import com.novoda.gradle.release.MavenPublicationAttachments
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc

class JavaAttachments extends MavenPublicationAttachments {

    JavaAttachments(String publicationName, Project project) {
        super(javaComponentFrom(project), getAllArtifactSources(publicationName, project))
    }

    private static List<Object> getAllArtifactSources(String publicationName, Project project, boolean uploadSourceAndDoc) {
        List<Object> allArtifactSources = new ArrayList<>()
        if (uploadSourceAndDoc) {
            allArtifactSources.add(javaSourcesJarTask(project, publicationName))
            allArtifactSources.add(javaJavadocsJarTask(project, publicationName))
        }
        return allArtifactSources
    }

    private static SoftwareComponent javaComponentFrom(Project project) {
        return project.components.getByName('java')
    }

    private static Task javaSourcesJarTask(Project project, String publicationName) {
        JavaCompile javaCompile = project.compileJava
        return sourcesJarTask(project, publicationName, javaCompile.source)
    }

    private static Task javaJavadocsJarTask(Project project, String publicationName) {
        Javadoc javadoc = project.javadoc
        return javadocsJarTask(project, publicationName, javadoc)
    }
}
