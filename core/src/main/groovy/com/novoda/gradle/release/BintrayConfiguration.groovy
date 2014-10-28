package com.novoda.gradle.release

import org.gradle.api.Project

class BintrayConfiguration {

    PublishExtension extension

    BintrayConfiguration(PublishExtension extension) {
        this.extension = extension
    }

    void configure(Project project) {
        if (extension.uploadName.isEmpty()) {
            extension.uploadName = extension.artifactId
        }

        project.bintray {
            user = getString(project, 'bintrayUser', extension.bintrayUser)
            key = getString(project, 'bintrayKey', extension.bintrayKey)
            publish = extension.autoPublish

            filesSpec {
                from extension.localReleasePath
                into "."
                exclude '**/maven-metadata.*'
            }

            pkg {
                repo = extension.repoType
                userOrg = extension.userOrg
                name = extension.uploadName
                desc = extension.description
                websiteUrl = extension.website
                issueTrackerUrl = extension.issueTracker
                vcsUrl = extension.repository

                licenses = extension.licences
                version {
                    name = extension.version
                }
            }
        }

        project.tasks.bintrayUpload.mustRunAfter(project.tasks.uploadArchives)
        project.gradle.taskGraph.useFilter { task ->
            shouldBeExecuted(task, project)
        }
    }

    String getString(Project project, String propertyName, String defaultValue) {
        project.hasProperty(propertyName) ? project.getProperty(propertyName) : defaultValue
    }

    def shouldBeExecuted(def task, Project project) {
        !isBintrayTask(task) || shouldPublishToBintray(project)
    }

    def isBintrayTask(def task) {
        task.name.equals('bintrayUpload')
    }

    boolean shouldPublishToBintray(Project project) {
        String propertyName = 'shouldUploadToBintray'
        boolean defaultValue = extension.shouldUploadToBintray
        project.hasProperty(propertyName) ? Boolean.valueOf(project.getProperty(propertyName)) : defaultValue
    }
}
