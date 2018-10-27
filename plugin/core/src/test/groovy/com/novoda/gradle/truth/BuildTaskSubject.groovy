package com.novoda.gradle.truth


import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import org.checkerframework.checker.nullness.compatqual.NullableDecl
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.TaskOutcome

import static com.google.common.truth.Fact.fact

class BuildTaskSubject extends Subject<BuildTaskSubject, BuildTask> {

    public static final Subject.Factory<BuildTaskSubject, BuildTask> FACTORY = { metadata, actual ->
        return new BuildTaskSubject(metadata, actual)
    }

    private BuildTaskSubject(FailureMetadata metadata, @NullableDecl BuildTask actual) {
        super(metadata, actual)
    }

    void hasOutcome(TaskOutcome outcome) {
        if (actual().outcome != outcome) {
            failWithoutActual(fact("expected outcome of task '${actual().path}' to be", outcome), fact('but was', actual().outcome))
        }
    }
}
