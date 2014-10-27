package com.novoda.gradle.release

class PublishExtention {

    String repoType = 'maven'
    String userOrg

    String groupId
    String artifactId
    String version
    String[] licences = ['Apache-2.0']

    String uploadName
    String description
    String website
    String issueTracker
    String repository
    boolean autoPublish = true

}