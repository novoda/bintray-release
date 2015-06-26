package com.novoda.gradle.release

import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree

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
                desc = extension.desc ?: extension.description
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
        if (extension.versionAttributes.isEmpty()) {
            FileTree pluginFiles = project.fileTree(dir: 'src/main/resources/META-INF/gradle-plugins')
            if (!pluginFiles.isEmpty()) {
                FileCollection filteredPluginFiles  = pluginFiles.filter {
                    it.name.endsWith(".properties") &&
                            it.name.substring(0, it.name.length() - 11).contains('.')
                }
                if (!filteredPluginFiles.isEmpty()) {
                    File bestPluginFile = filteredPluginFiles.first()
                    String pluginId = bestPluginFile.name.substring(0, bestPluginFile.name.length() - 11)
                    extension.versionAttributes << ['gradle-plugins': "$pluginId:$extension.groupId:$extension.artifactId"]
                    println "Using plugin identifier '" + extension.versionAttributes.get('gradle-plugins') + "' for gradle portal."
                }
            }
        }
    }
}
