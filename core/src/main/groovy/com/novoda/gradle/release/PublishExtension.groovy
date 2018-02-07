package com.novoda.gradle.release

class PublishExtension {

    String repoName = 'maven'
    String userOrg

    String groupId
    String artifactId

    String publishVersion

    Map<String, String> versionAttributes = [:]

    String[] licences = ['Apache-2.0']

    String uploadName = ''

    String desc

    String website = ''
    String issueTracker = ''
    String repository = ''
    boolean autoPublish = true

    String bintrayUser = ''
    String bintrayKey = ''
    boolean dryRun = true
    boolean override = false

    String[] publications

}
