package com.novoda.gradle.truth

import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.novoda.gradle.test.GradleBuildResult
import org.checkerframework.checker.nullness.compatqual.NullableDecl

class GradleBuildResultSubject extends Subject<GradleBuildResultSubject, GradleBuildResult> {

    public static final Subject.Factory<GradleBuildResultSubject, GradleBuildResult> FACTORY = { metadata, actual ->
        return new GradleBuildResultSubject(metadata, actual)
    }

    private GradleBuildResultSubject(FailureMetadata metadata, @NullableDecl GradleBuildResult actual) {
        super(metadata, actual)
    }

    void isSuccess() {
        check().that(actual().success).isTrue()
    }

    void isFailure() {
        check().that(actual().success).isFalse()
    }

    void containsOutput(String output) {
        check().that(actual().output).contains(output)
    }
}
