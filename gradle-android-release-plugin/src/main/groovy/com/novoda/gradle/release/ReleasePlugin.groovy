package com.novoda.gradle.release

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class ReleasePlugin implements Plugin<Project> {

    def void apply(Project project) {
        project.extensions.create('publish', PublishExtention)

        project.apply([plugin: "com.jfrog.bintray"])

        Task publishTask = project.task('publishReleaseToBintray', type: PublishTask)
        Task generateRelease = project.task('generateRelease', type: GenerateRelease)
        Task applyDependencies = project.task('applyDependencies', type: ApplyDependencies)
        Task bintrayUpload = project.tasks.getByName('bintrayUpload')
        Task build = project.tasks.getByName('build')
        Task uploadArchive = project.tasks.getByName('uploadArchives')

        uploadArchive.dependsOn(build)
        applyDependencies.dependsOn(uploadArchive)
        generateRelease.dependsOn(applyDependencies)

        bintrayUpload.dependsOn(generateRelease)
        publishTask.dependsOn(bintrayUpload)

        project.gradle.taskGraph.useFilter { task ->
            shouldBeExecuted(task, project)
        }

        project.afterEvaluate {
            def localReleaseDest = "${project.buildDir}/release"
            project.bintray {
                user = project.rootProject.bintrayUser
                key = project.rootProject.bintrayKey
                publish = project.publish.autoPublish

                filesSpec {
                    from localReleaseDest
                    into "."
                    exclude '**/maven-metadata.*'
                }

                pkg {
                    repo = project.publish.repoType
                    userOrg = project.publish.userOrg
                    name = project.publish.uploadName
                    desc = project.publish.description
                    websiteUrl = project.publish.website
                    issueTrackerUrl = project.publish.issueTracker
                    vcsUrl = project.publish.repository

                    licenses = project.publish.licences
                    version {
                        name = project.publish.version
                    }
                }
            }
        }
    }

    def static shouldBeExecuted(def task, def project) {
        !isBintrayRelated(task) || shouldPublishToBintray(project)
    }

    def static isBintrayRelated(def task) {
        task.name.toLowerCase().contains('bintray')
    }

    def static shouldPublishToBintray(def project) {
        project.rootProject.shouldUpload
    }

}