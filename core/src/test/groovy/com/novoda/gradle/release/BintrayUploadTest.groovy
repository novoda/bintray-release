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
class BintrayUploadTest {

    @Parameterized.Parameters(name = "{index}: test upload task for {0}")
    static Collection<Parameter> gradleVersionExpectedOutcome() {
        return [Parameter.forJavaProject(), Parameter.forAndroidProject()]
    }

    @Rule
    public final TestProjectRule testProject

    BintrayUploadTest(Parameter parameter) {
        this.testProject = parameter.rule
    }

    @Test
    void shouldUploadSuccessfullyAsDryRun() {
        def projectDir = testProject.projectDir
        def uploadTaskName = ":bintrayUpload"

        def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments('build', uploadTaskName, '-PbintrayUser=U', '-PbintrayKey=K', '-PdryRun=true', '--stacktrace')
                .forwardOutput()
                .withPluginClasspath()
                .build()

        assertThat(result.task(uploadTaskName).outcome).isEqualTo(TaskOutcome.SUCCESS)
    }

    private static class Parameter {

        static Parameter forJavaProject() {
            return new Parameter(TestProjectRule.newJavaProject())
        }

        static Parameter forAndroidProject() {
            return new Parameter(TestProjectRule.newAndroidProject())
        }

        final TestProjectRule rule

        Parameter(TestProjectRule rule) {
            this.rule = rule
        }

        String toString() {
            return rule.projectType
        }
    }
}
