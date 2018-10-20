package com.novoda.gradle.release

import com.novoda.gradle.release.test.BuildFolderRule
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class TestGeneratePomTask {

    @Rule
    public BuildFolderRule buildFolder = new BuildFolderRule('test-projects/TestGeneratePomTask')

    @Test
    void testGeneratePomTaskForJavaLib() {
        def projectDir = buildFolder.newFolder('testGeneratePomTaskForJavaLib')
        buildFolder.newFile(projectDir, 'build.gradle').write('''            

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
        '''.stripMargin())

        def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments('generatePomFileForMavenPublication', '--stacktrace')
                .forwardOutput()
                .withPluginClasspath()
                .build()

        assert result.task(':generatePomFileForMavenPublication').outcome == SUCCESS

        File pomFile = new File(projectDir, '/build/publications/maven/pom-default.xml')
        def nodes = new XmlSlurper().parse(pomFile)
        def dependencies = nodes.dependencies.dependency

        assert dependencies.size() == 3
        assert dependencies.find { dep -> dep.artifactId == 'hello' }.scope == 'compile'
        assert dependencies.find { dep -> dep.artifactId == 'haha' }.scope == 'compile'
        assert dependencies.find { dep -> dep.artifactId == 'world' }.scope == 'runtime'
    }

    @Test
    void testGeneratePomTaskForAndroidLibrary() {
        def projectDir = buildFolder.newFolder('testGeneratePomTaskForAndroidLibrary')
        File manifestFile = new File(projectDir, '/src/main/AndroidManifest.xml')
        manifestFile.getParentFile().mkdirs()
        manifestFile.createNewFile()
        manifestFile.write('''
            <manifest package="com.novoda.test"/>
        '''.stripMargin())

        buildFolder.newFile(projectDir, 'build.gradle').write('''
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
        '''.stripMargin())

        def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments('generatePomFileForReleasePublication', '--stacktrace')
                .forwardOutput()
                .withPluginClasspath()
                .build()

        assert result.task(':generatePomFileForReleasePublication').outcome == SUCCESS

        File pomFile = new File(projectDir, '/build/publications/release/pom-default.xml')
        def nodes = new XmlSlurper().parse(pomFile)
        def dependencies = nodes.dependencies.dependency

        assert dependencies.size() == 3
        assert dependencies.find { dep -> dep.artifactId == 'hello' }.scope == 'compile'
        assert dependencies.find { dep -> dep.artifactId == 'haha' }.scope == 'compile'
        assert dependencies.find { dep -> dep.artifactId == 'world' }.scope == 'runtime'
    }
}
