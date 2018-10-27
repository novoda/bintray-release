package com.novoda.release.internal.android.compat.gradle4_1


import org.gradle.api.internal.component.SoftwareComponentInternal
import org.gradle.api.internal.component.UsageContext

import javax.inject.Inject

class AndroidLibraryCompatGradle_4_1 implements SoftwareComponentInternal {

    @Inject
    AndroidLibraryCompatGradle_4_1() {
    }

    @Override
    String getName() {
        return 'android'
    }

    @Override
    Set<UsageContext> getUsages() {
        return Collections.emptySet()
    }
}
