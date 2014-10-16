package com.novoda.gradle.release
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

class ApplyDependencies extends DefaultTask {

    def localReleaseDest = "${project.buildDir}/release"

    @TaskAction
    def applyDependencies() {
        setupUploadArchives()
        if (project.plugins.hasPlugin('com.android.library')) {
            setupAndroidAar()
        } else {
            setupJar()
        }
    }

    private void setupJar() {
        Task sourcesJar = project.task('sourcesJar' ,type: Jar) {
            classifier = 'sources'
            from project.sourceSets.main.allSource
        }

        Task javadoc = project.tasks.findByName('javadoc')

        Task javadocJar = project.task('javadocJar', type: Jar) {
            classifier = 'javadoc'
            from javadoc.destinationDir
        }

        project.artifacts {
            archives sourcesJar
            archives javadocJar
        }
    }

    private void setupAndroidAar() {
        Task androidSourcesJar = project.task('androidSourcesJar', type: Jar) {
            classifier = 'sources'
            from project.android.sourceSets.main.java.srcDirs
        }

        Task androidJavadocs = project.task('androidJavadocs', type: Javadoc) {
            source = project.android.sourceSets.main.java.srcDirs
            classpath += project.files(project.android.getBootClasspath().join(File.pathSeparator))
        }

        Task androidJavadocsJar = project.task('androidJavadocsJar', type: Jar, dependsOn: androidJavadocs) {
            classifier = 'javadoc'
            from androidJavadocs.destinationDir
        }

        project.artifacts {
            archives androidSourcesJar
            archives androidJavadocsJar
        }
    }

    private void setupUploadArchives() {
        project.apply([plugin: "maven"])
        project.uploadArchives {
            repositories.mavenDeployer {
                pom.groupId = project.publish.groupId
                pom.artifactId = project.publish.artifactId
                pom.version = project.publish.version
                // Add other pom properties here if you want (developer details / licenses)
                repository(url: "file://${localReleaseDest}")
            }
        }
    }
}