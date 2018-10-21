package com.novoda.gradle.test

import org.gradle.testkit.runner.TaskOutcome

class GradleVerionsParams {

    String gradleVersion = "0"

    boolean expectedGradleBuildFailure = false

    TaskOutcome expectedTaskOutcome = TaskOutcome.FAILED

    @Override
    String toString() {
        return "GradleVerionsParams{" +
                "gradleVersion='" + gradleVersion + '\'' +
                ", expectedGradleBuildFailure=" + expectedGradleBuildFailure +
                ", expectedTaskOutcome=" + expectedTaskOutcome +
                '}'
    }
}
