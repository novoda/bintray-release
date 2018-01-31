package com.novoda.gradle.release

import org.assertj.core.api.Assertions
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.*
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4.class)
class JavaDifferentGradleVersions {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    private GradleRunner runner

    @Before
    void setUp() throws Exception {
        runner = GradleRunner.create()
                .withProjectDir(temporaryFolder.root)
                .withArguments("build", "bintrayUpload", "-PbintrayKey=key", "-PbintrayUser=user")
                .withPluginClasspath()

        def buildFile = temporaryFolder.newFile("build.gradle")
        buildFile.write(
                """            
            plugins { 
                id 'java'
                id 'com.novoda.bintray-release' version '0.8.0' 
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

    @Test
    void test_withGradle40_shouldSucceed() {
        def result = runner.withGradleVersion("4.0").build()

        Assertions.assertThat(result.task(":bintrayUpload").outcome).isEqualTo(TaskOutcome.SUCCESS)
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
    void test_withGradle45_shouldSucceed() {
        def result = runner.withGradleVersion("4.5").build()

        Assertions.assertThat(result.task(":bintrayUpload").outcome).isEqualTo(TaskOutcome.SUCCESS)
    }
}
