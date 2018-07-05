package com.novoda.gradle.release

import guru.stefma.androidartifacts.AndroidArtifactsExtension

class AndroidPublishExtension extends PublishExtension {

    private AndroidArtifactsExtension mAndroidExtension

    AndroidPublishExtension(AndroidArtifactsExtension androidArtifactsExtension) {
        mAndroidExtension = androidArtifactsExtension
    }

    @Override
    void setArtifactId(String newArtifactId) {
        super.setArtifactId(newArtifactId)
        mAndroidExtension.artifactId = newArtifactId
    }
}
