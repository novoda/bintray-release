package com.novoda.gradle.release

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

final class LibProjectFactory {

    public static Project createFromFixture() {
        Project project = ProjectBuilder.builder().withProjectDir(new File(TestConfiguration.FIXTURE_WORKING_DIR)).build()
        project.apply plugin: 'com.android.library'
        project.version = "1.0"
        project.group = "com.novoda.demo.gradle.release"
        project.android {
            compileSdkVersion 23
            buildToolsVersion '23.0.0'

            defaultConfig {
                versionCode 1
                versionName '1.0'
                minSdkVersion 23
                targetSdkVersion 23
            }
        }

        project.repositories {
            jcenter()
        }

        project.dependencies {
            compile 'com.novoda:download-manager:0.2.38'
            releaseCompile 'com.google.code.gson:gson:2.5'
        }

        return project
    }

    public static Project createFromFixtureWithFlavors() {
        Project project = ProjectBuilder.builder().withProjectDir(new File(TestConfiguration.FIXTURE_WORKING_DIR)).build()
        project.apply plugin: 'com.android.library'
        project.version = "1.0"
        project.group = "com.novoda.demo.gradle.release"
        project.android {
            compileSdkVersion 23
            buildToolsVersion '23.0.0'

            defaultConfig {
                versionCode 1
                versionName '1.0'
                minSdkVersion 23
                targetSdkVersion 23
            }

            productFlavors {
                flavor1{}
                flavor2{}
            }
        }

        project.repositories {
            jcenter()
        }

        project.dependencies {
            compile 'com.novoda:download-manager:0.2.38'
            flavor1Compile 'com.google.code.gson:gson:2.5'

        }

        return project

    }
}
