package com.novoda.gradle.release

import groovy.transform.PackageScope

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

    /**
     * Validate all mandatory properties for this extension.
     *
     * Will throw a Exception if not setup correctly.
     */
    @PackageScope
    void validate() {
        String extensionError = "";
        if (userOrg == null) {
            extensionError += "Missing userOrg. "
        }
        if (groupId == null) {
            extensionError += "Missing groupId. "
        }
        if (artifactId == null) {
            extensionError += "Missing artifactId. "
        }
        if (publishVersion == null) {
            extensionError += "Missing publishVersion. "
        }
        if (desc == null) {
            extensionError += "Missing desc. "
        }

        if (extensionError) {
            String prefix = "Have you created the publish closure? "
            throw new IllegalStateException(prefix + extensionError)
        }
    }

}
