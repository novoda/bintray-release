# bintray-release [ ![Download](https://api.bintray.com/packages/ooftf/maven/bintray-release/images/download.svg) ](https://bintray.com/ooftf/maven/bintray-release/_latestVersion)

Super duper easy way to release your Android and other artifacts to bintray.


## Description

Support gradle 6.1.1



```groovy
apply plugin: 'com.novoda.bintray-release' // must be applied after your artifact generating plugin (eg. java / com.android.library)

buildscript {
    repositories {
        maven {
            url "https://dl.bintray.com/ooftf/maven"
        }
    }
    dependencies {
        classpath 'com.ooftf:bintray-release:<latest-version>'
    }
}
```

## Simple usage

Use the `publish` closure to set the info of your package:

```groovy
publish {
    userOrg = 'novoda'
    groupId = 'com.novoda'
    artifactId = 'bintray-release'
    publishVersion = '0.6.1'
    desc = 'Oh hi, this is a nice description for a project, right?'
    website = 'https://github.com/novoda/bintray-release'
}
```

If you use [Kotlin DSL](https://github.com/gradle/kotlin-dsl) use:

```kotlin
import com.novoda.gradle.release.PublishExtension

configure<PublishExtension> {
  userOrg = "novoda"
  groupId = "com.novoda"
  artifactId = "bintray-release"
  publishVersion = "0.6.1"
  desc = "Oh hi, this is a nice description for a project, right?"
  website = "https://github.com/novoda/bintray-release"
}
```

Finally, use the task `bintrayUpload` to publish (make sure you build the project first!):

```bash
$ ./gradlew clean build bintrayUpload -PbintrayUser=BINTRAY_USERNAME -PbintrayKey=BINTRAY_KEY -PdryRun=false
```

More info on the available properties and other usages in the [Github Wiki](https://github.com/novoda/bintray-release/wiki).

## Gradle compatibility

The plugin officially supports only Gradle 4.0+
