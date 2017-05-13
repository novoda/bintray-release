package com.novoda.gradle.release

import com.jfrog.bintray.gradle.BintrayPlugin
import guru.stefma.androidartifacts.ArtifactsExtension
import guru.stefma.androidartifacts.CreateAndroidArtifactsExtensions
import org.gradle.api.Project

class ReleasePlugin extends CreateAndroidArtifactsExtensions {

    PublishExtension mPublishExtension

    @Override
    void apply(Project project) {
        super.apply(project)

        project.afterEvaluate {
            new BintrayPlugin().apply(project)
            new BintrayConfiguration(mPublishExtension).configure(project)
        }
    }

    @Override
    ArtifactsExtension getArtifactsExtensions(Project project) {
        mPublishExtension = project.extensions.create('publish', PublishExtension)
        return mPublishExtension
    }

}
