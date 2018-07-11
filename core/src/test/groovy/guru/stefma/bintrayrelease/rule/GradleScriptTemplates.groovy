package guru.stefma.bintrayrelease.rule

import groovy.transform.PackageScope

class GradleScriptTemplates {

    @PackageScope
    static String java() {
        return """
            plugins { 
                id 'java-library'
                id 'guru.stefma.bintrayrelease'
            }
            
            repositories {
                jcenter()
            }
            
            dependencies {
                implementation "junit:junit:4.12"
            }
            
            group = "guru.stefma"
            version = "1.0"
            publish {
                userOrg = 'stefma'
                artifactId = 'test'
                desc = 'description'
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
                id 'guru.stefma.bintrayrelease' apply false
            }
            
            apply plugin: "com.android.library"
            apply plugin: "guru.stefma.bintrayrelease"
            
            android {
                compileSdkVersion 26

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
                google()
            }
            
            dependencies {
                implementation "junit:junit:4.12"
            }
            
            group = "guru.stefma"
            version = "1.0"
            publish {
                userOrg = 'stefma'
                artifactId = 'test'
                desc = 'description'
            }
               """
    }
}
