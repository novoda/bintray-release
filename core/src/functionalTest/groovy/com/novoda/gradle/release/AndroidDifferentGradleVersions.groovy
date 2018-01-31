package com.novoda.gradle.release

import org.assertj.core.api.Assertions
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.*
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4.class)
class AndroidDifferentGradleVersions {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    private GradleRunner runner

    @Before
    void setUp() throws Exception {
        runner = GradleRunner.create()
                .withProjectDir(temporaryFolder.root)
                .withArguments("build", "bintrayUpload", "-PbintrayKey=key", "-PbintrayUser=user")
                .withPluginClasspath()

        File manifestFile = new File(temporaryFolder.root, "/src/main/AndroidManifest.xml")
        manifestFile.getParentFile().mkdirs()
        manifestFile.createNewFile()
        manifestFile.write('<manifest package="com.novoda.test"/>')

        def buildFile = temporaryFolder.newFile("build.gradle")
        buildFile.write(
                """            
            buildscript {
                repositories {
                    jcenter()
                    google()
                }
                dependencies {
                    classpath 'com.android.tools.build:gradle:3.0.0'
                    classpath 'com.novoda:bintray-release:0.8.0'
                }
            }
            
            apply plugin: "com.android.library"
            apply plugin: "com.novoda.bintray-release"
            
            android {
                compileSdkVersion 26
                buildToolsVersion "26.0.2"

                defaultConfig {
                    minSdkVersion 16
                    versionCode 1
                    versionName "0.0.1"
                }    
                
                lintOptions {
                   abortOnError false
                }
            }
            
            repositories {
                jcenter()
            }
            
            dependencies {
                implementation "junit:junit:4.12"
            }
            
            publish {
                userOrg = 'novoda'
                groupId = 'com.novoda'
                artifactId = 'test'
                publishVersion = '1.0'
            }
                    """
        )

        def fakeSourceCode = new File(temporaryFolder.root, "src/main/java/HelloWorld.java")
        fakeSourceCode.getParentFile().mkdirs()
        fakeSourceCode.createNewFile()
        fakeSourceCode.write(" public class HelloWorld {} ")
    }

    @After
    void tearDown() throws Exception {
        temporaryFolder.delete()
    }

    /**
     * Android don't support only gradle 4.1 and up with plugin 3.0
     */
    @Test
    void test_withGradle40_shouldFail() {
        runner.withGradleVersion("4.0").buildAndFail()
    }

    @Test
    void test_withGradle41_shouldSucceed() {
        def result = runner.withGradleVersion("4.1").build()

        Assertions.assertThat(result.task(":bintrayUpload").outcome).isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    void test_withGradle42_shouldSucceed() {
        def result = runner.withGradleVersion("4.2").build()

        Assertions.assertThat(result.task(":bintrayUpload").outcome).isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    void test_withGradle43_shouldSucceed() {
        def result = runner.withGradleVersion("4.3").build()

        Assertions.assertThat(result.task(":bintrayUpload").outcome).isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    void test_withGradle44_shouldSucceed() {
        def result = runner.withGradleVersion("4.4").build()

        Assertions.assertThat(result.task(":bintrayUpload").outcome).isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    @Ignore("Plugin is currently broken with 4.5. UsageContext have changed...")
    void test_withGradle45_shouldSucceed() {
        def result = runner.withGradleVersion("4.5").build()

        Assertions.assertThat(result.task(":bintrayUpload").outcome).isEqualTo(TaskOutcome.SUCCESS)
    }
}
