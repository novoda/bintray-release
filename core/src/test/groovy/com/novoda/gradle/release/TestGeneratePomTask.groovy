package com.novoda.gradle.release

import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class TestGeneratePomTask {

    @Rule public TemporaryFolder testProjectDir = new TemporaryFolder()
    private File buildFile

    @Before
    void setup() {
        buildFile = testProjectDir.newFile('build.gradle')
    }

    @Test
    void testGeneratePomTaskForJavaLib() {
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
                desc = 'description'
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
            .withPluginClasspath()
            .build()

        assert result.task(":generatePomFileForMavenPublication").outcome == SUCCESS

        File pomFile = new File(testProjectDir.root, '/build/publications/maven/pom-default.xml')
        def nodes = new XmlSlurper().parse(pomFile)
        def dependencies = nodes.dependencies.dependency

        assert dependencies.size() == 3
        assert dependencies.find { dep -> dep.artifactId == "hello" && dep.scope == "compile" } != null
        assert dependencies.find { dep -> dep.artifactId == "haha" && dep.scope == "compile" } != null
        assert dependencies.find { dep -> dep.artifactId == "world" && dep.scope == "runtime" } != null
    }

    @Test
    void testGeneratePomTaskForAndroidLibrary() {
        File manifestFile = new File(testProjectDir.root, "/src/main/AndroidManifest.xml")
        manifestFile.getParentFile().mkdirs()
        manifestFile.createNewFile()
        manifestFile << """
            <manifest package="com.novoda.test"/>
        """

        buildFile << """
            buildscript {
                repositories {
                    jcenter()
                    google()
                }
                dependencies {
                    classpath 'com.android.tools.build:gradle:3.0.0'
                }
            }
            
            plugins {
                id 'bintray-release' apply false
            }
            
            apply plugin: "com.android.library"
            apply plugin: "bintray-release"
            
            android {
                compileSdkVersion 26
                buildToolsVersion "26.0.2"

                defaultConfig {
                    minSdkVersion 16
                    versionCode 1
                    versionName "0.0.1"
                }    
            }
            
            publish {
                userOrg = 'novoda'
                groupId = 'com.novoda'
                artifactId = 'test'
                publishVersion = '1.0'
                desc = 'description'
            }
        
            dependencies {
                compile 'com.abc:hello:1.0.0'
                implementation 'com.xyz:world:2.0.0'
                api 'com.xxx:haha:3.0.0'
            }
        """

        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("generatePomFileForReleasePublication")
            .withPluginClasspath()
            .build()

        assert result.task(":generatePomFileForReleasePublication").outcome == SUCCESS

        File pomFile = new File(testProjectDir.root, '/build/publications/release/pom-default.xml')
        def nodes = new XmlSlurper().parse(pomFile)
        def dependencies = nodes.dependencies.dependency

        assert dependencies.size() == 3
        assert dependencies.find { dep -> dep.artifactId == "hello" && dep.scope == "compile" } != null
        assert dependencies.find { dep -> dep.artifactId == "haha" && dep.scope == "compile" } != null
        assert dependencies.find { dep -> dep.artifactId == "world" && dep.scope == "runtime" } != null
    }
}