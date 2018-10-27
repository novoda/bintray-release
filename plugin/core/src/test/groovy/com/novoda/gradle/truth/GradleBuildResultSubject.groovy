package com.novoda.gradle.truth


import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.novoda.gradle.test.GradleBuildResult
import org.checkerframework.checker.nullness.compatqual.NullableDecl

import static com.google.common.truth.Fact.fact

class GradleBuildResultSubject extends Subject<GradleBuildResultSubject, GradleBuildResult> {

    public static final Subject.Factory<GradleBuildResultSubject, GradleBuildResult> FACTORY = { metadata, actual ->
        return new GradleBuildResultSubject(metadata, actual)
    }

    private GradleBuildResultSubject(FailureMetadata metadata, @NullableDecl GradleBuildResult actual) {
        super(metadata, actual)
    }

    void isSuccess() {
        if (!actual().success) {
            failWithoutActual(fact('expected build status to be', 'BUILD SUCCESSFUL'), fact('but was', 'BUILD FAILED'))
        }
    }

    void isFailure() {
        if (actual().success) {
            failWithoutActual(fact('expected build status to be', 'BUILD FAILED'), fact('but was', 'BUILD SUCCESSFUL'))
        }
    }

    void containsOutput(String output) {
        if (!actual().output.contains(output)) {
            failWithoutActual(fact('expected build output to contain ', output), fact('but was', actual().output))
        }
    }
}
