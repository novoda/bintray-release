package com.novoda.gradle.release
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GenerateRelease extends DefaultTask {

    @TaskAction
    def generate() {
        def localReleaseDest = "${project.buildDir}/release"
        println  "Release ${project.publish.version} can be found at ${localReleaseDest}/"
    }
}