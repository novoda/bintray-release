package com.novoda.gradle.release

import com.jfrog.bintray.gradle.BintrayExtension
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

        def bintrayExtension = project.extensions.findByName("bintray") as BintrayExtension
        bintrayExtension.with {
            user = propertyFinder.bintrayUser
            key = propertyFinder.bintrayKey
            publish = extension.autoPublish
            dryRun = propertyFinder.dryRun
            override = propertyFinder.override

            publications = extension.publications ?: project.plugins.hasPlugin('com.android.library') ? ['release'] : ['maven']

            pkg.with {
                repo = extension.repoName
                userOrg = extension.userOrg
                name = extension.uploadName
                desc = extension.desc
                websiteUrl = extension.website
                issueTrackerUrl = extension.issueTracker
                vcsUrl = extension.repository

                licenses = extension.licences
                version.with {
                    name = propertyFinder.publishVersion
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
            def gradlePluginPropertyFinder = new GradlePluginPropertyFinder(project)
            String bestPluginId = gradlePluginPropertyFinder.findBestGradlePluginId()
            if (bestPluginId != null) {
                extension.versionAttributes << ['gradle-plugin': "$bestPluginId:$extension.groupId:$extension.artifactId"]
                project.logger.info "Using plugin identifier '" + extension.versionAttributes.get('gradle-plugins') + "' for gradle portal."
            }
        }
    }
}
