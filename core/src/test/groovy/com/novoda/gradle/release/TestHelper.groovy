package com.novoda.gradle.release

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

final class TestHelper {

    public static Project evaluatableLibProject() {
        Project project = ProjectBuilder.builder().withProjectDir(new File(TestBintrayRelease.FIXTURE_WORKING_DIR)).build()
        project.apply plugin: 'com.android.library'
        project.version = "1.0"
        project.group = "com.example.novoda.gradle.release"
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
