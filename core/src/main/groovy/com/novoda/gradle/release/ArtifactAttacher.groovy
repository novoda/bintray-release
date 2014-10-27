package com.novoda.gradle.release;

import org.gradle.api.Project

interface ArtifactAttacher {
    void attachTo(Project project)
}
