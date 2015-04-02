package com.novoda.gradle.release

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static junit.framework.TestCase.assertFalse

public class ReleasePluginTest {
    Project projectJava
    Project projectAndroidLibrary

    @Before
    public void setupProject() {
        projectJava = ProjectBuilder.builder().withProjectDir(new File("testProjectJava")).build()
        def pluginJava = projectJava.plugins.apply(ReleasePlugin)
        projectJava.publish.version = "1.0"
        projectJava.apply plugin:'java'

        projectAndroidLibrary = ProjectBuilder.builder().withProjectDir(new File("testProjectAndroidLibrary")).build()
        def pluginAndroidLibrary = projectAndroidLibrary.plugins.apply(ReleasePlugin)
        projectAndroidLibrary.apply plugin: 'com.android.library'
    }

    @Test
    public void shouldAddBintrayUploadTask() {
        def tasks = projectJava.tasks.getByName("bintrayUpload").findAll { true }
        assertFalse tasks.isEmpty()
    }
}
