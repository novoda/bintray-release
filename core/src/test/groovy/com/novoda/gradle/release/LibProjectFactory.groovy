package com.novoda.gradle.release

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

final class LibProjectFactory {

    public static Project createFromFixture() {
        Project project = ProjectBuilder.builder().withProjectDir(new File(TestBintrayRelease.FIXTURE_WORKING_DIR)).build()
        project.apply plugin: 'com.android.library'
        project.version = "1.0"
        project.group = "com.novoda.gradle.release.demo"
        project.android {
            compileSdkVersion 23
            buildToolsVersion '23.0.0'

            defaultConfig {
                versionCode 1
                versionName '1.0'
                minSdkVersion 23
                targetSdkVersion 23
            }

            buildTypes {
                release {
                    signingConfig signingConfigs.debug
                }
            }
        }

        return project
    }
}
