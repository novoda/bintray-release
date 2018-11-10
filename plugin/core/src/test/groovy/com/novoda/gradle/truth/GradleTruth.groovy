package com.novoda.gradle.truth

import com.google.common.truth.BooleanSubject
import com.google.common.truth.TruthJUnit
import com.novoda.gradle.test.GradleBuildResult
import org.gradle.testkit.runner.BuildTask

import static com.google.common.truth.Truth.assertAbout

final class GradleTruth {

    static GradleBuildResultSubject assertThat(GradleBuildResult result) {
        return assertAbout(GradleBuildResultSubject.FACTORY).that(result)
    }

    static BooleanSubject assumeThat(boolean flag) {
        return TruthJUnit.assume().that(flag)
    }

    static BuildTaskSubject assertThat(BuildTask actual) {
        return assertAbout(BuildTaskSubject.FACTORY).that(actual)
    }
}
