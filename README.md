android-bintray-release [![](http://ci.novoda.com/buildStatus/icon?job=android-bintray-release)](http://ci.novoda.com/job/android-bintray-release/lastSuccessfulBuild/console)
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

Configuration
---

The `publish` closure contains all these properties. The default values are empty unless specified otherwise:

 * `repoType`: The repo type. Set to `'maven'` by default.
 * `userOrg`: Contains the organisation name to use for upload.
 * `groupId`: The group id to use for the upload.
 * `artifactId`: The artifact id to use.
 * `version`: A string with the version to use. Can't end with `-SNAPSHOT` because bintray doesn't accept snapshots.
 * `licences`: A list of license identifiers for the project. Identifiers can be found here: http://spdx.org/licenses/ and default value is `['Apache-2.0']`.
 * `uploadName`: The display name for this package in bintray. If not set, the `artifactId` will be used for this.
 * `description`: A short description for this package in bintray.
 * `website`: A string with the url for the website of this project. The Github repo can be used here.
 * `issueTracker`: The url of the issue tracker for the project. If the `website` contains `'github.com'` then this property is set to `"${website}/issues"` by default.
 * `repository`: The url of the vcs for this project. If the `website` contains `'github.com'` then this is set to `"${website}.git"` by default.
 * `autoPublish`: This boolean defines if the package will be published when uploaded. If this is `false`, the package will still be uploaded to bintray but you'll have to publish it manually. Default value is `true`.
 * `bintrayUser`: The username to be used to upload.
 * `bintrayKey`: The bintray API key for the user account. Explanation of where to find this value can be found here https://bintray.com/docs/interacting/interacting_apikeys.html
 * `dryRun`: If set to `true` this will run everything but it won't upload the package to bintray. If `false` then it will upload normally.
 * `publications`: A list of publication names to use for the upload. The default value is `['maven']`, which is a publication that this plugin creates for you. **You can define your own publications**, more info [in the wiki page](https://github.com/novoda/android-bintray-release/wiki/Defining-a-custom-Publication)

License
=================================
```
(c) Copyright 2014 Novoda

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
