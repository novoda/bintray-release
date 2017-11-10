package com.novoda.gradle.release

import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class TestGeneratePomTask {

    @Rule public TemporaryFolder testProjectDir = new TemporaryFolder()
    private String pluginClasspath
    private File buildFile

    @Before
    void setup() {
        if (pluginClasspath == null) {
            def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
            if (pluginClasspathResource == null) {
                throw new IllegalStateException("Did not find plugin classpath resource, run `testClasses` build task.")
            }
            def classpath = pluginClasspathResource.readLines().collect { new File(it) }
            pluginClasspath = classpath
                .collect { it.absolutePath.replace('\\', '\\\\') } // escape backslashes in Windows paths
                .collect { "'$it'" }
                .join(", ")
        }
        buildFile = testProjectDir.newFile('build.gradle')
    }

    @Test
    void testGeneratePomTaskForJavaLib() {
        buildFile << """
            buildscript {
                dependencies {
                    classpath files($pluginClasspath)
                }
            }
            
            apply plugin: "java-library"
            apply plugin: "bintray-release"
            
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
                    classpath files($pluginClasspath)
                    classpath 'com.android.tools.build:gradle:3.0.0'
                }
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
            .build()

        assert result.task(":generatePomFileForReleasePublication").outcome == SUCCESS

        File pomFile = new File(testProjectDir.root, '/build/publications/release/pom-default.xml')
        def nodes = new XmlSlurper().parse(pomFile)
        def dependencies = nodes.dependencies.dependency

        assert dependencies.size() == 3
    }
}