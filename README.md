# android-bintray-release [![](http://ci.novoda.com/buildStatus/icon?job=android-bintray-release)](http://ci.novoda.com/job/android-bintray-release/lastSuccessfulBuild/console)

Super duper easy way to release your Android and other artifacts to bintray.


## Description

This is a helper for releasing libraries to bintray. It is intended to help configuring stuff related to maven and bintray.
At the moment it works with Android Library projects, plain Java and plain Groovy projects, but our focus is to mainly support Android projects.


## Adding to project

To publish a library to bintray using this plugin, add these dependencies to the `build.gradle` of the module that will be published:

```groovy
apply plugin: 'android-bintray-release' // must be applied after your artifact generating plugin (eg. java / com.android.library)

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.novoda:android-bintray-release:0.2.3'
    }
}
```


## Simple usage

Use the `publish` closure to set the info of your package:

```groovy
publish {
    userOrg = 'novoda'
    groupId = 'com.novoda'
    artifactId = 'android-bintray-release'
    version = '0.2.3'
    description = 'Oh hi, this is a nice description for a project right?'
    website = 'https://github.com/novoda/android-bintray-release'
}
```

Finally, use the task `bintrayUpload` to publish (make sure you build the project first!):

```bash
$ ./gradlew clean build bintrayUpload -PbintrayUser=BINTRAY_USERNAME -PbintrayKey=BINTRAY_KEY -PdryRun=false
```

Note that you have to pass in some parameters:

 * `bintrayUser`: Specifies the bintray username that will perform the upload
 * `bintrayKey`: Specifies the bintray auth key for `bintrayUser`
 * `dryRun`: Default is `true`. If set to `false`, this will perform the upload, if set to `true` it won't actually upload. This is useful to set up in your CI so that you can upload manually without having the lib published on each merge of a PR for example. These values can be set in the `publish` closure too or you can set them in your global `gradle.properties` file.


## Links

Here are a list of useful links:

 * Contributing `// TODO`
 * [Github Issues](https://github.com/novoda/android-bintray-release/issues)
 * [Github Wiki](https://github.com/novoda/android-bintray-release/wiki)
 * Stack Overflow Tag: `// TODO`


## License

Copyright &copy; 2014 [Novoda](http://novoda.com/blog/) Ltd. Released under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
