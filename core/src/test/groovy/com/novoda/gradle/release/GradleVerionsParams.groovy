package com.novoda.gradle.release

import org.gradle.testkit.runner.TaskOutcome

class GradleVerionsParams {

    final String gradleVersion

    final boolean expectedGradleBuildFailure

    final TaskOutcome expectedTaskOutcome

    GradleVerionsParams(String gradleVersion, TaskOutcome expectedTaskOutcome) {
        this(gradleVersion, expectedTaskOutcome, false)
    }

    GradleVerionsParams(String gradleVersion, boolean expectedGradleBuildFailure) {
        this(gradleVersion, TaskOutcome.NO_SOURCE, expectedGradleBuildFailure)
    }

    private GradleVerionsParams(String gradleVersion, TaskOutcome expectedTaskOutcome, boolean expectedGradleBuildFailure) {
        this.gradleVersion = gradleVersion
        this.expectedGradleBuildFailure = expectedGradleBuildFailure
        this.expectedTaskOutcome = expectedTaskOutcome
    }

    @Override
    String toString() {
        return "GradleVerionsParams{" +
                "gradleVersion='" + gradleVersion + '\'' +
                ", expectedGradleBuildFailure=" + expectedGradleBuildFailure +
                ", expectedTaskOutcome=" + expectedTaskOutcome +
                '}'
    }
}
