package com.novoda.gradle.release.rule

import groovy.transform.PackageScope

class GradleScriptTemplates {

    @PackageScope
    static String java() {
        return """
            plugins { 
                id 'java-library'
                id 'com.novoda.bintray-release'
            }
            
            repositories {
                jcenter()
            }
            
            dependencies {
                implementation "junit:junit:4.12"
            }
            
            publish {
                userOrg = 'novoda'
                groupId = 'com.novoda'
                artifactId = 'test'
                publishVersion = '1.0'
            }
               """
    }

    @PackageScope
    static String android() {
        return """
            buildscript {
                repositories {
                    jcenter()
                    google()
                }
                dependencies {
                    classpath 'com.android.tools.build:gradle:3.0.0'
                }
            }
            
            plugins {
                id 'com.novoda.bintray-release' apply false
            }
            
            apply plugin: "com.android.library"
            apply plugin: "com.novoda.bintray-release"
            
            android {
                compileSdkVersion 26
                buildToolsVersion "26.0.2"

                defaultConfig {
                    minSdkVersion 16
                    versionCode 1
                    versionName "0.0.1"
                }    
                
                lintOptions {
                   tasks.lint.enabled = false
                }
            }
            
            repositories {
                jcenter()
            }
            
            dependencies {
                implementation "junit:junit:4.12"
            }
            
            publish {
                userOrg = 'novoda'
                groupId = 'com.novoda'
                artifactId = 'test'
                publishVersion = '1.0'
                desc = 'description'
            }
               """
    }
}
