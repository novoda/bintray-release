package com.novoda.gradle.release

import org.gradle.api.Project

class BintrayConfiguration {

    PublishExtension extension

    BintrayConfiguration(PublishExtension extension) {
        this.extension = extension
    }

    void configure(Project project) {
        initDefaults()

        project.bintray {
            user = getString(project, 'bintrayUser', extension.bintrayUser)
            key = getString(project, 'bintrayKey', extension.bintrayKey)
            publish = extension.autoPublish

            publications = extension.publications

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

    private void initDefaults() {
        if (extension.uploadName.isEmpty()) {
            extension.uploadName = extension.artifactId
        }

        if (extension.website.contains('github.com')) {
            if (extension.issueTracker.isEmpty()) {
                extension.issueTracker = "${extension.website}/issues"
            }
            if (extension.repository.isEmpty()) {
                extension.repository = "${extension.website}.git"
            }
        }
    }

    String getString(Project project, String propertyName, String defaultValue) {
        project.hasProperty(propertyName) ? project.getProperty(propertyName) : defaultValue
    }

    def shouldBeExecuted(def task, Project project) {
        !isBintrayTask(task) || !isDryRun(project)
    }

    def isBintrayTask(def task) {
        task.name.equals('bintrayUpload')
    }

    boolean isDryRun(Project project) {
        String propertyName = 'dryRun'
        boolean defaultValue = extension.dryRun
        project.hasProperty(propertyName) ? Boolean.valueOf(project.getProperty(propertyName)) : defaultValue
    }
}
