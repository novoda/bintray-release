package com.novoda.gradle.release

import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class TestGeneratePomTask {

    @Rule public TemporaryFolder testProjectDir = new TemporaryFolder()
    private List<File> pluginClasspath
    private File buildFile

    @Before
    void setup() {
        if (pluginClasspath == null) {
            def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
            if (pluginClasspathResource == null) {
                throw new IllegalStateException("Did not find plugin classpath resource, run `testClasses` build task.")
            }
            pluginClasspath = pluginClasspathResource.readLines().collect { new File(it) }

            buildFile = testProjectDir.newFile('build.gradle')
        }
    }

    @Test
    void testGeneratePomTask() {
        buildFile << """
            plugins {
                id 'java-library'
                id 'bintray-release'
            }
            
            publish {
                userOrg = 'novoda'
                groupId = 'com.novoda'
                artifactId = 'test'
                publishVersion = '1.0'
            }
        
            dependencies {
                compile 'com.abc:hello:1.0.0'
                implementation 'com.xyz:world:2.0.0'
                api 'com.xxx:haha:3.0.0'
            }
        """

        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("generatePomFileForMavenPublication")
            .withPluginClasspath(pluginClasspath)
            .build()

        print(testProjectDir)
        assert result.task(":generatePomFileForMavenPublication").outcome == SUCCESS

        File pomFile = new File(testProjectDir.root, '/build/publications/maven/pom-default.xml')
        def nodes = new XmlSlurper().parse(pomFile)
        def dependencies = nodes.dependencies.dependency

        assert dependencies.size() == 3
        assert dependencies.find { dep -> dep.artifactId == "hello" && dep.scope == "compile" } != null
        assert dependencies.find { dep -> dep.artifactId == "haha" && dep.scope == "compile" } != null
        assert dependencies.find { dep -> dep.artifactId == "world" && dep.scope == "runtime" } != null
    }
}