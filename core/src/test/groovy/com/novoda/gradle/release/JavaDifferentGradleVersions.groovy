package com.novoda.gradle.release

import com.novoda.gradle.release.test.TestProjectRule
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import static com.google.common.truth.Truth.assertThat

@RunWith(Parameterized.class)
class JavaDifferentGradleVersions {

    @Rule
    public TestProjectRule projectRule = TestProjectRule.newJavaProject()

    @Parameterized.Parameters(name = "{index}: test Gradle version {0}")
    static Collection<GradleVerionsParams> gradleVersionExpectedOutcome() {
        return [
                new GradleVerionsParams(gradleVersion: "4.0", expectedTaskOutcome: TaskOutcome.SUCCESS),
                new GradleVerionsParams(gradleVersion: "4.1", expectedTaskOutcome: TaskOutcome.SUCCESS),
                new GradleVerionsParams(gradleVersion: "4.2", expectedTaskOutcome: TaskOutcome.SUCCESS),
                new GradleVerionsParams(gradleVersion: "4.3", expectedTaskOutcome: TaskOutcome.SUCCESS),
                new GradleVerionsParams(gradleVersion: "4.4", expectedTaskOutcome: TaskOutcome.SUCCESS),
                new GradleVerionsParams(gradleVersion: "4.5", expectedTaskOutcome: TaskOutcome.SUCCESS),
        ]
    }

    private GradleVerionsParams testParams

    JavaDifferentGradleVersions(GradleVerionsParams testParams) {
        this.testParams = testParams
    }

    @Test
    void givenGradleVersion_WhenProjectBuild_ShouldHaveExpectedOutcome() {
        def runner = GradleRunner.create()
                .withProjectDir(projectRule.projectDir)
                .withArguments("build", "bintrayUpload", "-PbintrayKey=key", "-PbintrayUser=user")
                .withPluginClasspath()
                .withGradleVersion(testParams.gradleVersion)
        if (testParams.expectedGradleBuildFailure) {
            runner.buildAndFail()
        } else {
            assertThat(runner.build().task(":bintrayUpload").outcome).isEqualTo(testParams.expectedTaskOutcome)
        }
    }

}
