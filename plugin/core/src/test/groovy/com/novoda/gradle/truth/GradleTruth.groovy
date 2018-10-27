package com.novoda.gradle.truth

import com.novoda.gradle.test.GradleBuildResult
import org.gradle.testkit.runner.BuildTask

import static com.google.common.truth.Truth.assertAbout

final class GradleTruth {

    static GradleBuildResultSubject assertThat(GradleBuildResult result) {
        return assertAbout(GradleBuildResultSubject.FACTORY).that(result)
    }

    static BuildTaskSubject assertThat(BuildTask actual) {
        return assertAbout(BuildTaskSubject.FACTORY).that(actual)
    }
}
