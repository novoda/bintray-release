package com.novoda.gradle.release

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.maven.tasks.GenerateMavenPom
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

import static com.google.common.truth.Truth.assertThat

class TestConfiguration {

    // This is necessary because the IDE debugger and command line invocations have different working directories ಠ_ಠ
    private static final WORKING_DIR = System.getProperty("user.dir")
    private static final PATH_PREFIX = WORKING_DIR.endsWith("core") ? WORKING_DIR : "$WORKING_DIR/core"

    static final String FIXTURE_WORKING_DIR = "$PATH_PREFIX/src/test/fixtures/android_app"
    static Project project
    static Project flavorProject

    @BeforeClass
    public static void setup() {
        project = LibProjectFactory.createFromFixture()
        ReleasePlugin plugin = new ReleasePlugin()
        plugin.apply(project)
        project.publish {
            userOrg = 'novoda'
            groupId = 'com.novoda.demo'
            artifactId = "test"
            publishVersion = project.version
            description = 'Sample library'
            dryRun = true
        }
        project.evaluate()

        flavorProject = LibProjectFactory.createFromFixtureWithFlavors()
        plugin.apply(flavorProject)
        flavorProject.publish {
            userOrg = 'novoda'
            groupId = 'com.novoda.demo'
            artifactId = "test-flavors"
            publishVersion = project.version
            description = 'Sample library'
            dryRun = true
        }
        flavorProject.evaluate()

    }

    @AfterClass
    static void tearDown() {
        project.buildDir.deleteDir();

        // delete folders generated during project evaluation
        new File("${FIXTURE_WORKING_DIR}/userHome").deleteDir()
        new File("${FIXTURE_WORKING_DIR}/.gradle").deleteDir()
    }

    @Test
    public void testBintrayConfiguration() {
        assertThat(project.publish.repoName).isEqualTo("maven")
        assertThat(project.bintray.pkg.repo).isEqualTo("maven")
        assertThat(project.bintray.pkg.userOrg).isEqualTo("novoda")
        assertThat(project.bintray.pkg.name).isEqualTo("test")
    }

    @Test
    public void testGeneratedPomFileForMavenPublicationTask() {
        testGeneratedPomFileForVariantPublicationTask(project, "Release", "test", "release", "com.novoda:download-manager:0.2.38", "com.google.code.gson:gson:2.5");
        testGeneratedPomFileForVariantPublicationTask(flavorProject, "Flavor1Release", "test-flavor1", "flavor1Release", "com.novoda:download-manager:0.2.38", "com.google.code.gson:gson:2.5");
        testGeneratedPomFileForVariantPublicationTask(flavorProject, "Flavor2Release", "test-flavor2", "flavor2Release", "com.novoda:download-manager:0.2.38");
    }

    private static void testGeneratedPomFileForVariantPublicationTask(Project project, String variantName, String artifactId, String publicationName, String... dependencies) {
        Task task = project.getTasks().findByPath(":generatePomFileFor${variantName}Publication")
        assertThat(task).isNotNull()

        GenerateMavenPom generatePomTask = task as GenerateMavenPom
        generatePomTask.execute()

        File pomFile = new File(project.buildDir, "/publications/${publicationName}/pom-default.xml")
        assertThat(pomFile.exists())
        NodeList nodes = new XmlParser().parse(pomFile).depthFirst()
        assertThat(nodes).isNotNull()
        assertThat(nodes).hasSize 6 + (dependencies.length > 0 ? 1 : 0) + dependencies.length * 5

        // Skip the first two since they're boilerplate xml stuff
        Node groupIdNode = nodes[2]
        assertThat(groupIdNode.name().localPart).isEqualTo "groupId"
        assertThat(groupIdNode.value()[0]).isEqualTo "com.novoda.demo"
        Node artifactIdNode = nodes[3]
        assertThat(artifactIdNode.name().localPart).isEqualTo "artifactId"
        assertThat(artifactIdNode.value()[0]).isEqualTo artifactId
        Node versionNode = nodes[4]
        assertThat(versionNode.name().localPart).isEqualTo "version"
        assertThat(versionNode.value()[0]).isEqualTo "1.0"
        Node packagingNode = nodes[5]
        assertThat(packagingNode.name().localPart).isEqualTo "packaging"
        assertThat(packagingNode.value()[0]).isEqualTo "aar"

        // Dependencies
        Node dependenciesNode = nodes[6];
        assertThat(dependenciesNode.name().localPart).isEqualTo "dependencies"

        // check gson
        for (int i = 0; i < dependencies.length; i++) {
            assertDependency(nodes, 7 + i * 5, dependencies[i]);
        }

    }

    private static void assertDependency(NodeList nodes, Integer startIndex, String dependency) {
        String[] parts = dependency.split(":")
        assertNode(nodes[startIndex], "dependency", null)
        assertNode(nodes[startIndex + 1], "groupId", parts[0])
        assertNode(nodes[startIndex + 2], "artifactId", parts[1])
        assertNode(nodes[startIndex + 3], "version", parts[2])
        assertNode(nodes[startIndex + 4], "scope", "runtime")
    }

    private static void assertNode(node, String localName, String value) {
        assertThat(node.name().localPart).isEqualTo localName
        if (value)
            assertThat(node.value()[0]).isEqualTo value
    }

    @Test
    public void testProjectHasBintrayUploadTask() {
        Task task = project.getTasks().findByPath(":bintrayUpload")
        assertThat(task).isNotNull()

        // there is nothing to unit test because all the magic happens in a build listener
        //    that is added in the BintrayPlugin
    }
}
