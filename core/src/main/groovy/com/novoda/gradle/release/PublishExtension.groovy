package com.novoda.gradle.release

import org.gradle.api.Project

class PublishExtension {

    String repoName = 'maven'
    String userOrg
    String groupId

    String artifactId
    @Deprecated
    String version

    /**
     *  @deprecated due to conflicts with gradle project.version. replaced by {@link #publishVersion}
     *  https://github.com/novoda/bintray-release/issues/43
     */
    String publishVersion;
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

    def getBintrayUser() {
        prop('bintrayUser')
    }

    def getBintrayKey() {
        prop('bintrayKey')
    }

    def getDryRun() {
        prop('dryRun')
    }

    def getPublishVersion() {
        prop('publishVersion') ?: version
    }

    private String prop(String propertyName) {
        project.hasProperty(propertyName) ? project.property(propertyName) : this.@"__${propertyName}__"
    }

}
