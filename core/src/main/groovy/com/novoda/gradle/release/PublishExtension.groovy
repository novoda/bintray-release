package com.novoda.gradle.release

import org.gradle.api.Project

class PublishExtension {

    String repoName = 'maven'
    String userOrg
    String groupId

    String artifactId

    /**
     *  @deprecated due to conflicts with gradle project.version. replaced by {@link #publishVersion}
     *  https://github.com/novoda/bintray-release/issues/43
     */
    @Deprecated
    String version

    def publishVersion

    String[] licences = ['Apache-2.0']

    String uploadName = ''

    String description
    String website = ''
    String issueTracker = ''
    String repository = ''
    boolean autoPublish = true
    String bintrayUser

    String bintrayKey
    boolean dryRun = true
    String[] publications = ['maven']

    private Project project

    PublishExtension(Project project) {
        this.project = project
    }

    @Override
    Object getProperty(final String property) {
        return project.hasProperty(property) ? project.property(property) : super.getProperty(property)
    }

}
