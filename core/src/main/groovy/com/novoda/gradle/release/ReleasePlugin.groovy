package com.novoda.gradle.release

import com.jfrog.bintray.gradle.BintrayPlugin
import com.novoda.gradle.release.artifacts.AndroidArtifacts
import com.novoda.gradle.release.artifacts.Artifacts
import com.novoda.gradle.release.artifacts.JavaArtifacts
import com.novoda.gradle.release.artifacts.PublishExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.util.GradleVersion

class ReleasePlugin implements Plugin<Project> {

    void apply(Project project) {
        PublishExtension extension = project.extensions.create('publish', PublishExtension)
        project.afterEvaluate {
            project.apply([plugin: 'maven-publish'])
            attachArtifacts(extension, project)
            new BintrayPlugin().apply(project)
            new BintrayConfiguration(extension).configure(project)
        }
    }

    void attachArtifacts(PublishExtension extension, Project project) {
        if (project.plugins.hasPlugin('com.android.library')) {
            def current = GradleVersion.current()
            def version3_3 = GradleVersion.version("3.3")

            if ((current <=> version3_3) <= 0) {
                // detected Gradle 3.3 or smaller
                // show message for required gradle upgrade
                // details: https://github.com/novoda/bintray-release/issues/112
                System.err.println("""
                        |**************************************
                        |WARNING: $current not supported by bintray-release plugin. Update required!
                        |
                        |The bintray-release plugin doesn't support version of Gradle below 3.4 for Android libraries. Please upgrade to Gradle 3.4 or later.
                        |The last bintray-release plugin supporting Gradle 3.3 is 'com.novoda:bintray-release:4.0'
                        |
                        |Upgrade Gradle:
                        |./gradlew wrapper --gradle-version 3.5 --distribution-type all
                        |
                        |The bintray-release plugin can't create a Publication for your Android Library with $current!
                        |**************************************
                        """.stripMargin());
                return;
            }
            project.android.libraryVariants.each { variant ->
                def artifactId = extension.artifactId;
                addArtifact(project, variant.name, artifactId, new AndroidArtifacts(variant))
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
