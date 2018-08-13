[![CircleCI](https://circleci.com/gh/StefMa/bintray-release.svg?style=svg)](https://circleci.com/gh/StefMa/bintray-release)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
# BintrayRelease
A super duper easy way to release your Android and Java artifacts to [Bintray](https://bintray.com).


## Description
This is a helper for releasing Android and Java libraries to Bintray. 
Basically it is "just" a wrapper around the [Gradle-Bintray-Plugin](https://github.com/bintray/gradle-bintray-plugin) and the [AndroidArtifacts Plugin](https://github.com/StefMa/AndroidArtifacts).

The Plugin configure all artifacts for you and hock it into the `Gradle-Bintay-Plugin`.

At this time of writing it supports "all Android" libraries which will be supported by the `AndroidArtifacts` Plugin.
Beside of that it will create artifacts for Java libraries. But later this should be part of the `AndroidArtifacts` Plugin.

## How to use it
### Apply the Plugin
Put the following lines to your **project** `build.gradle`:

```groovy
apply plugin: "com.android.library"
apply plugin: "guru.stefma.bintrayrelease" //1

buildscript {
    repositories {
        jcenter()
        google()
    }
    dependencies {
        // The current version can be found here https://git.io/fNUnx
        classpath "guru.stefma.bintrayrelease:bintrayrelease:$bintrayReleaseVersion"
    }
}
```

### Configure the `publish` extension
The Plugin brings a `publish` extension which needs to be setup in your **module** `build.gradle` in the following way:

```groovy
version = "1.0.0"
group = "guru.stefma.bintrayrelease"
publish {
    userOrg = 'stefma'
    artifactId = 'bintrayrelease'
    desc = 'Oh hi, this is a nice description for a project, right?'
    website = 'https://github.com/stefma/bintray-release'
}

```

### Publish
Finally, use the task `bintrayUpload` to publish (make sure you build the project first!):

```
./gradlew clean build bintrayUpload -PbintrayUser=BINTRAY_USERNAME -PbintrayKey=BINTRAY_KEY -PdryRun=false
```

## About the Fork
This is a fork of the original [`bintray-release`](https://github.com/novoda/bintray-release) of [**novoda**](https://novoda.com/).
I started to create and maintain this fork (instead of contribute there) because I wanted to have the "full control" over the project.
Beside of this this fork use another Plugin as dependency which I've developed by myself. To keep it regularly in sync with 
the "upstream Plugin" I thought it is easy to have a Plugin which is owned by me.

### License  
Because it is a fork of the `bintray-release` (which is original licensed under the `Apache-2`) I decided to license these
Plugin under the Apache-2 as well.

I don't want to violence the Apache-2. But I'm also not a lawyer. Because of this - and the lack of knowledge - I decided to keep
**novoda** inside the [LICENSE](LICENSE.txt). 

But not all code in this Plugin is original "novoda code". I started the fork at the commit [`0d998ad`](https://github.com/StefMa/bintray-release/commit/0d998ad9cf4f822be2bcbffaf02bbee881f13101).
Feel free to compare the latest version with the commit to see a diff about "what is new and what is original novoda software".   
