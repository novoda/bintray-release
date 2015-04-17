package com.novoda.gradle.release

import org.gradle.api.Project

class PublishExtension {

    public final String NAME = 'publish'

    String repoName = 'maven'
    String userOrg
    String groupId

    String artifactId

    /**
     * @deprecated due to conflicts with gradle project.version. replaced by {@link #publishVersion}
     *  https://github.com/novoda/bintray-release/issues/43
     */
    @Deprecated
    String version

    String publishVersion

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
        initDefaults()
    }

    private void initDefaults() {
        // properties can be set via -P commandline param
        // (using extension name as prefix, e.g. -Ppublish.bintrayUser="me")
        project.properties.findAll { key, value ->
            key.startsWith("${NAME}.")
        }.each { key, value ->
            String prop = key -  "${NAME}."
            delegate[prop] = value
        }
    }
}
