package com.novoda.gradle.test


import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.TaskOutcome

class GradleBuildResult implements BuildResult {

    final boolean success
    private final BuildResult buildResult

    GradleBuildResult(BuildResult buildResult, boolean success = true) {
        this.buildResult = buildResult
        this.success = success
    }

    @Override
    String getOutput() {
        return buildResult.getOutput()
    }

    @Override
    List<BuildTask> getTasks() {
        return buildResult.getTasks()
    }

    @Override
    List<BuildTask> tasks(TaskOutcome taskOutcome) {
        return buildResult.tasks(taskOutcome)
    }

    @Override
    List<String> taskPaths(TaskOutcome taskOutcome) {
        return buildResult.taskPaths(taskOutcome)
    }

    @Override
    BuildTask task(String s) {
        return buildResult.task(s)
    }
}

