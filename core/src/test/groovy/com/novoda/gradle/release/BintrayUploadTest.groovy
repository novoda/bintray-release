package com.novoda.gradle.release

import com.novoda.gradle.release.test.TestProjectRule
import com.novoda.gradle.truth.GradleTruth
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

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
        def uploadTaskName = ":bintrayUpload"

        def result = testProject.execute('build', uploadTaskName, '-PbintrayUser=U', '-PbintrayKey=K', '-PdryRun=true', '--stacktrace')

        GradleTruth.assertThat(result).isSuccess()
        GradleTruth.assertThat(result.task(uploadTaskName)).hasOutcome(TaskOutcome.SUCCESS)
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
