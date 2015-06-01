package com.novoda.gradle.release

import org.gradle.api.Project

class BintrayConfiguration {

    PublishExtension extension

    BintrayConfiguration(PublishExtension extension) {
        this.extension = extension
    }

    void configure(Project project) {
        initDefaults()
        deriveDefaultsFromProject(project)

        PropertyFinder propertyFinder = new PropertyFinder(project, extension)

        project.bintray {
            user = propertyFinder.getBintrayUser()
            key = propertyFinder.getBintrayKey()
            publish = extension.autoPublish
            dryRun = propertyFinder.getDryRun()

            publications = extension.publications

            pkg {
                repo = extension.repoName
                userOrg = extension.userOrg
                name = extension.uploadName
                desc = extension.description
                websiteUrl = extension.website
                issueTrackerUrl = extension.issueTracker
                vcsUrl = extension.repository

                licenses = extension.licences
                version {
                    name = propertyFinder.getPublishVersion()
                    attributes = extension.versionAttributes
                }
            }
        }
        project.tasks.bintrayUpload.mustRunAfter(project.tasks.uploadArchives)
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

    private void deriveDefaultsFromProject(Project project) {
        if (!project.fileTree(dir: 'src/main/resources/META-INF/gradle-plugins').isEmpty()) {
            extension.versionAttributes << ['gradle-plugins': '${extension.userOrg}:${extension.groupId}:${extension.artifactId}']
        }
    }
}
