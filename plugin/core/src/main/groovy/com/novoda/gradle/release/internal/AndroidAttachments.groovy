package com.novoda.gradle.release.internal

import com.novoda.gradle.release.MavenPublicationAttachments
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.tasks.javadoc.Javadoc

class AndroidAttachments extends MavenPublicationAttachments {

    private static final String ANDROID_SOFTWARE_COMPONENT_COMPAT_4_1 = 'com.novoda.release.internal.compat.gradle4_1.AndroidSoftwareComponentCompat_Gradle_4_1'

    AndroidAttachments(String publicationName, Project project, def variant) {
        super(androidComponentFrom(project),
                androidSourcesJarTask(project, publicationName, variant),
                androidJavadocsJarTask(project, publicationName, variant),
                androidArchivePath(variant))
    }

    private static SoftwareComponent androidComponentFrom(Project project) {
        def clazz = this.classLoader.loadClass(ANDROID_SOFTWARE_COMPONENT_COMPAT_4_1)
        return clazz.newInstance(project.objects, project.configurations) as SoftwareComponent
    }

    private static Task androidSourcesJarTask(Project project, String publicationName, def variant) {
        def sourcePaths = variant.sourceSets.collect { it.javaDirectories }.flatten()
        return sourcesJarTask(project, publicationName, sourcePaths)
    }

    private static Task androidJavadocsJarTask(Project project, String publicationName, def variant) {
        Javadoc javadoc = project.task("javadoc${publicationName.capitalize()}", type: Javadoc) { Javadoc javadoc ->
            javadoc.source = variant.javaCompiler.source
            javadoc.classpath = variant.javaCompiler.classpath
        } as Javadoc
        return javadocsJarTask(project, publicationName, javadoc)
    }

    private static def androidArchivePath(def variant) {
        return variant.outputs[0].packageLibrary
    }
}
