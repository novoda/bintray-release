package com.novoda.gradle.release
import com.jfrog.bintray.gradle.BintrayUploadTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.maven.tasks.GenerateMavenPom
import org.junit.AfterClass
import org.junit.Test

import static com.google.common.truth.Truth.assertThat

class TestBintrayRelease {

    // This is necessary because the IDE debugger and command line invocations have different working directories ಠ_ಠ
    private static final WORKING_DIR = System.getProperty("user.dir")
    private static final PATH_PREFIX = WORKING_DIR.endsWith("core") ? WORKING_DIR : "$WORKING_DIR/core"

    static final String FIXTURE_WORKING_DIR = "$PATH_PREFIX/src/test/fixtures/android_app"

    @AfterClass
    static void tearDown() {
        // I don't know why this gets generated, but toss it
        new File("${FIXTURE_WORKING_DIR}/userHome").deleteDir()
        new File("${FIXTURE_WORKING_DIR}/.gradle").deleteDir()
    }

    @Test
    public void testConfiguration() {
        Project project = LibProjectFactory.createFromFixture()
        ReleasePlugin plugin = new ReleasePlugin()
        plugin.apply(project)
        project.publish {
            userOrg = 'novoda'
            groupId = 'com.example.novoda'
            artifactId = "test"
            version = project.version
            description = 'Super duper easy way to release your Android and other artifacts to bintray'
            website = "https://github.com/novoda/bintray-release"
            dryRun = true
        }
        project.evaluate()

        Task task1 = project.getTasks().findByPath(":bintrayUpload")
        assertThat(task1).isNotNull()
        Task task2 = project.getTasks().findByPath(":generatePomFileForMavenPublication")
        assertThat(task2).isNotNull()

        // Now assert some other stuff about the tasks
        BintrayUploadTask uploadTask = task1 as BintrayUploadTask

        GenerateMavenPom generatePomTask = task2 as GenerateMavenPom
        generatePomTask.execute()
        File pomFile = new File(project.buildDir, "/publications/maven/pom-default.xml")
        assertThat(pomFile.exists())
        NodeList nodes = new XmlParser().parse(pomFile).depthFirst()
        assertThat(nodes).isNotNull()
        assertThat(nodes).hasSize 6

        // Skip the first two since they're boilerplate xml stuff
        Node groupIdNode = nodes[2]
        assertThat(groupIdNode.name().localPart).isEqualTo "groupId"
        assertThat(groupIdNode.value()[0]).isEqualTo "com.example.novoda"
        Node artifactIdNode = nodes[3]
        assertThat(artifactIdNode.name().localPart).isEqualTo "artifactId"
        assertThat(artifactIdNode.value()[0]).isEqualTo "test"
        Node versionNode = nodes[4]
        assertThat(versionNode.name().localPart).isEqualTo "version"
        assertThat(versionNode.value()[0]).isEqualTo "1.0"
        Node packagingNode = nodes[5]
        assertThat(packagingNode.name().localPart).isEqualTo "packaging"
        assertThat(packagingNode.value()[0]).isEqualTo "aar"

        // uploadTask verification here

        project.buildDir.deleteDir()
    }
}
