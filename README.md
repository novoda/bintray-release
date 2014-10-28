android-bintray-release
=============================

A helper for releasing from gradle on bintray. It is intended to help configuring stuff related to maven and bintray.
At the moment it works at least in Android Library projects and plain Java projects. This plugin is currently work-in-progress.

Usage
=============================
To publish a library on bintray using this plugin, add these dependencies to the `build.gradle` of the module that will be published:

```groovy
apply plugin: 'com.android.library'
apply plugin: 'android-bintray-release'

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.jfrog.bintray.gradle:gradle-bintray-plugin:0.6'
        classpath 'com.novoda:android-bintray-release:0.0.5'
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
    issueTracker = "${website}/issues"
    repository = "${website}.git"
}
```

Finally, use the task `bintrayUpload` to publish:
```bash
$ gradlew build bintrayUpload -PbintrayUser=USERNAME -PbintrayKey=BINTRAY_KEY -PshouldUploadToBintray=true
```

Note that you have to pass in some parameters:

 * `bintrayUser`: Specifies the bintray username that will perform the upload
 * `bintrayKey`: Specifies the bintray auth key for `bintrayUser`
 * `shouldUploadToBintray`: If set to `true`, this will perform the upload, if set to `false` it won't actually upload. This is useful to set up
 in your CI so that you can upload manually without having the lib published on each merge of a PR for example.

If your project is not open source, you can also specify the credentials in the `publish` closure:

```groovy
publish {
    bintrayUser = 'username'
    bintrayKey = 'thisisareallylonglongkey'
}
```
