package com.novoda.gradle.release

import guru.stefma.androidartifacts.ArtifactsExtension

class PublishExtension extends ArtifactsExtension {

    String repoName = 'maven'

    String userOrg

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

    String[] publications

}
