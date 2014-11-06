android-bintray-release
=============================

Super duper easy way to release your Android and other artifacts to bintray.

This is a helper for releasing libraries to bintray. It is intended to help configuring stuff related to maven and bintray.
At the moment it works with Android Library projects, plain Java and plain Groovy projects, but our focus is to mainly support Android projects.

Note: This plugin is currently work-in-progress, so some things might not work or it might have bugs.

Usage
=============================
To publish a library to bintray using this plugin, add these dependencies to the `build.gradle` of the module that will be published:

```groovy
apply plugin: 'com.android.library' // Can be java or groovy for non android projects
apply plugin: 'android-bintray-release'

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.novoda:android-bintray-release:0.2.3'
    }
}
```

And add the publish configuration for the project:

```groovy
publish {
    userOrg = 'myorg'
    groupId = 'com.myorg'
    artifactId = 'artifact-name'
    version = '0.0.1'
    description = 'Oh hi, this is a nice description for a project right?'
    website = 'https://github.com/myorg/artifact-name'
    issueTracker = "${website}/issues"   // optional - this is automatically setup for github websites
    repository = "${website}.git"        // optional - this is automatically setup for github websites
}
```

Finally, use the task `bintrayUpload` to publish (make sure you build the project first!):
```bash
$ ./gradlew clean build bintrayUpload -PbintrayUser=USERNAME -PbintrayKey=BINTRAY_KEY -PdryRun=false
```

Note that you have to pass in some parameters:

 * `bintrayUser`: Specifies the bintray username that will perform the upload
 * `bintrayKey`: Specifies the bintray auth key for `bintrayUser`
 * `dryRun`: Default is `true`. If set to `false`, this will perform the upload, if set to `true` it won't actually upload. This is
 useful to set up in your CI so that you can upload manually without having the lib published on each merge of a PR for example.

If your project is not open source, you can also specify the credentials in a properties file or in the `publish` closure:

```groovy
publish {
    bintrayUser = 'username'
    bintrayKey = 'thisisareallylonglongkey'
}
```
