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
class JavaDifferentGradleVersions {

    @Rule
    public TestProjectRule projectRule = new TestProjectRule(TestProjectRule.Project.JAVA)

    @Parameterized.Parameters(name = "{index}: test Gradle version {0}")
    static Collection<GradleVerionsParams> gradleVersionExpectedOutcome() {
        return [
                new GradleVerionsParams("4.0", TaskOutcome.SUCCESS),
                new GradleVerionsParams("4.1", TaskOutcome.SUCCESS),
                new GradleVerionsParams("4.2", TaskOutcome.SUCCESS),
                new GradleVerionsParams("4.3", TaskOutcome.SUCCESS),
                new GradleVerionsParams("4.4", TaskOutcome.SUCCESS),
                new GradleVerionsParams("4.5", TaskOutcome.SUCCESS),
        ]
    }

    private GradleVerionsParams testParams

    JavaDifferentGradleVersions(GradleVerionsParams testParams) {
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
