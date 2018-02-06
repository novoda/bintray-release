package com.novoda.gradle.release

import com.novoda.gradle.release.rule.TestProjectRule
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import static org.assertj.core.api.Assertions.assertThat

@RunWith(Parameterized.class)
class AndroidDifferentGradleVersions {

    @Rule
    public TestProjectRule projectRule = new TestProjectRule(TestProjectRule.Project.ANDROID)

    @Parameterized.Parameters(name = "{index}: test Gradle version {0}")
    static Collection<GradleVerionsParams> gradleVersionExpectedOutcome() {
        return [
                // Gradle 4.0 is not support by the Android Gradle Plugin 3.x
                new GradleVerionsParams(gradleVersion: "4.0", expectedGradleBuildFailure: true),
                new GradleVerionsParams(gradleVersion: "4.1", expectedTaskOutcome: TaskOutcome.SUCCESS),
                new GradleVerionsParams(gradleVersion: "4.2", expectedTaskOutcome: TaskOutcome.SUCCESS),
                new GradleVerionsParams(gradleVersion: "4.3", expectedTaskOutcome: TaskOutcome.SUCCESS),
                new GradleVerionsParams(gradleVersion: "4.4", expectedTaskOutcome: TaskOutcome.SUCCESS),
                // TODO: Failure on Gradle 4.5. is **not** expected. It's failing because of changes in
                // the UsageContext in our AndroidLibrary class
                new GradleVerionsParams(gradleVersion: "4.5", expectedGradleBuildFailure: true),
        ]
    }

    private GradleVerionsParams testParams

    AndroidDifferentGradleVersions(GradleVerionsParams testParams) {
        this.testParams = testParams
    }

    @Test
    void testDifferentGradleVersionsAndOutcome() {
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
