package com.novoda.gradle.release


import org.gradle.api.component.SoftwareComponent
import org.gradle.api.publish.maven.MavenPublication

abstract class MavenPublicationAttachments {

    abstract List<Object> getAllArtifactSources()

    abstract SoftwareComponent getSoftwareComponent()

    void attachTo(MavenPublication publication) {
        allArtifactSources.each { publication.artifact it }
        publication.from softwareComponent
    }
}
