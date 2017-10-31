package com.novoda.gradle.release

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Test

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

public class TestBintrayUploadTask {

    @Test
    public void testBintrayUploadTask() {
        BuildResult result = runTasksOnBintrayReleasePlugin('-PbintrayUser=U', '-PbintrayKey=K', "bintrayUpload")

        def successfulTasks = result.tasks(SUCCESS).collect { it.path }
        assert successfulTasks.contains(":bintrayUpload")
        assert result.getOutput().contains("BUILD SUCCESSFUL")
    }

    BuildResult runTasksOnBintrayReleasePlugin(String... arguments) {
        File file = getAbsoluteDirectoryOfOurProjectBase()

        GradleRunner runner = GradleRunner.create()
                .withProjectDir(file)

        if (arguments) {
            runner.withArguments(arguments)
        }
        return runner.build()
    }

    /**
     * Get a path that is absolute when running this test from the IDE or the CMD line and work back from there
     */
    private File getAbsoluteDirectoryOfOurProjectBase() {
        def fileDir = getClass().protectionDomain.codeSource.location.path
        return new File(fileDir + "../../../..")
    }
}
