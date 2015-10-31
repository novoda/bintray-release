package com.novoda.gradle.release

import com.novoda.gradle.test.IntegrationTest
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.GradleRunner
import org.junit.Test
import org.junit.experimental.categories.Category

import static com.google.common.truth.Truth.assertThat
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;

@Category(IntegrationTest.class)
public class TestBintrayUploadTask {

    @Test
    public void testBintrayUploadTask() {
        BuildResult result = runTasksOnBintrayReleasePlugin('-PbintrayUser=U', '-PbintrayKey=K', "bintrayUpload")

        assertThat(result.tasks(SUCCESS).collect {it.path}).contains(":core:bintrayUpload")
        assertThat(result.getStandardOutput()).contains("BUILD SUCCESSFUL")
    }

    BuildResult runTasksOnBintrayReleasePlugin(String... arguments) {
        GradleRunner runner = GradleRunner.create()
                .withProjectDir(new File(".."))
        if (arguments) {
            runner.withArguments(arguments)
        }
        return runner.build()
    }
}
