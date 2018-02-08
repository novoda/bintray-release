package com.novoda.gradle.release;

import org.gradle.api.Project
import org.gradle.api.component.SoftwareComponent

interface Artifacts {

    def all(String publicationName, Project project)

    SoftwareComponent from(Project project)

}
