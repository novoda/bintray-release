package guru.stefma.bintrayrelease

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Ignore
import org.junit.Test

public class TestBintrayUploadTask {

    @Ignore("Ignored since the 'pase project' don't use *this* plugin as dependency")
    @Test
    public void testBintrayUploadTask() {
        BuildResult result = runTasksOnBintrayReleasePlugin('-PbintrayUser=U', '-PbintrayKey=K', "bintrayUpload")

        TaskOutcome success = TaskOutcome.SUCCESS
        List<BuildTask> tasks = result.tasks(success)
        List<String> successfulTaskPaths = new ArrayList<>();
        for (BuildTask task : tasks) {
            successfulTaskPaths.add(task.path)
        }

        assert successfulTaskPaths.contains(":bintrayUpload")
        assert result.getOutput().contains("BUILD SUCCESSFUL")
    }

    BuildResult runTasksOnBintrayReleasePlugin(String... arguments) {
        File file = getAbsoluteDirectoryOfOurProjectBase()

        GradleRunner runner = GradleRunner.create()
                .withProjectDir(file)
                .withPluginClasspath()

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
