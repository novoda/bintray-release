package com.novoda.gradle.release;

import org.gradle.api.Project

interface Artifacts {

    def all(String publicationName, Project project)

}
