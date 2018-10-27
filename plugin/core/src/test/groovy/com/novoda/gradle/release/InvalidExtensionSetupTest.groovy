package com.novoda.gradle.release

import com.novoda.gradle.test.TestProject
import com.novoda.gradle.truth.GradleTruth
import org.junit.Rule
import org.junit.Test

class InvalidExtensionSetupTest {

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
               """.stripIndent()

    @Rule
    public TestProject testProject = TestProject.newJavaProject(buildScript)

    @Test
    void shouldFailWhenMissingMandatoryAttributes() {
        def result = testProject.execute(':build')

        GradleTruth.assertThat(result).isFailure()
        GradleTruth.assertThat(result).containsOutput("Have you created the publish closure? Missing publishVersion. Missing desc. ")
    }
}
