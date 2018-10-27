package com.novoda.gradle.release

import com.novoda.gradle.test.GradleScriptTemplates
import com.novoda.gradle.test.TestProjectRule
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import static com.google.common.truth.Truth.assertThat

@RunWith(Parameterized.class)
class GradleVersionsCompatibilityTest {

    @Parameterized.Parameters(name = "{0}")
    static Collection<BuildConfiguration> configurations() {
        return [
                BuildConfiguration.forAndroid('4.0', false),
                BuildConfiguration.forAndroid('4.1', true),
                BuildConfiguration.forAndroid('4.2', true),
                BuildConfiguration.forAndroid('4.3', true),
                BuildConfiguration.forAndroid('4.4', true),
                BuildConfiguration.forAndroid('4.5', false),
                BuildConfiguration.forJava('4.0', true),
                BuildConfiguration.forJava('4.1', true),
                BuildConfiguration.forJava('4.2', true),
                BuildConfiguration.forJava('4.3', true),
                BuildConfiguration.forJava('4.4', true),
                BuildConfiguration.forJava('4.5', true)
        ]
    }

    @Rule
    public final TestProjectRule testProject
    private final BuildConfiguration configuration

    GradleVersionsCompatibilityTest(BuildConfiguration configuration) {
        this.configuration = configuration
        this.testProject = configuration.testProject
    }

    @Test
    void shouldMatchExpectedOutcome() {
        def result = testProject.execute("build", "bintrayUpload", "-PbintrayKey=key", "-PbintrayUser=user")

        assertThat(result.success).isEqualTo(configuration.expectedBuildSuccess)
    }

    private static class BuildConfiguration {
        final String gradleVersion
        final TestProjectRule testProject
        final boolean expectedBuildSuccess

        static BuildConfiguration forAndroid(String gradleVersion, boolean expectedBuildSuccess) {
            def additionalRunnerConfig = { GradleRunner runner -> runner.withGradleVersion(gradleVersion) }
            def projectRule = TestProjectRule.newAndroidProject(GradleScriptTemplates.forAndroidProject(), additionalRunnerConfig)
            return new BuildConfiguration(gradleVersion, projectRule, expectedBuildSuccess)
        }

        static BuildConfiguration forJava(String gradleVersion, boolean expectedBuildSuccess) {
            def additionalRunnerConfig = { GradleRunner runner -> runner.withGradleVersion(gradleVersion) }
            def projectRule = TestProjectRule.newJavaProject(GradleScriptTemplates.forJavaProject(), additionalRunnerConfig)
            return new BuildConfiguration(gradleVersion, projectRule, expectedBuildSuccess)
        }

        private BuildConfiguration(String gradleVersion, TestProjectRule testProject, boolean expectedBuildSuccess) {
            this.gradleVersion = gradleVersion
            this.testProject = testProject
            this.expectedBuildSuccess = expectedBuildSuccess
        }

        @Override
        String toString() {
            return "${testProject.projectType.capitalize()} project with Gradle $gradleVersion"
        }
    }
}
