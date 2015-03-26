package com.novoda.gradle.release

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static junit.framework.TestCase.assertFalse

public class ReleasePluginTest {
    Project project

    @Before
    public void setupProject() {
        project = ProjectBuilder.builder().withProjectDir(new File("testProject")).build()
        def plugin = project.plugins.apply(ReleasePlugin)
        project.apply plugin:'java'
    }

    @Test
    public void shouldAddBintrayUploadTask() {
        def tasks = project.tasks.getByName("bintrayUpload").findAll { true }
        assertFalse tasks.isEmpty()
    }
}
