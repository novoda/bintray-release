package com.novoda.gradle.release

import com.novoda.gradle.release.rule.TestProjectRule
import org.assertj.core.api.Assertions
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import static org.assertj.core.api.Assertions.*

@RunWith(JUnit4.class)
class TestInvalidExtensionSetup {

    private String buildScript = """
            plugins { 
                id 'java-library'
                id 'com.novoda.bintray-release'
            }
            
            publish {
                userOrg = 'novoda'
                groupId = 'com.novoda'
                artifactId = 'test'
                // publishVersion = '1.0'
                // desc = 'description'
            }
               """

    @Rule
    public TestProjectRule projectRule = new TestProjectRule(TestProjectRule.Project.JAVA, buildScript)

    @Test
    void testInvalidExtension_versionAndDescMissing_ShouldFailWithCorrectMessage() {
        def result = GradleRunner.create()
                .withProjectDir(projectRule.projectDir)
                .withArguments("build")
                .withPluginClasspath()
                .buildAndFail()

        assertThat(result.output).contains("Have you created the publish closure? Missing publishVersion. Missing desc. ")
    }
}
