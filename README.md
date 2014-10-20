gradle-android-release-plugin
=============================

A helper for releasing from gradle on bintray.
This plugin is currently work-in-progress.

Usage
=============================
To publish a library on bintray using this plugin, add these dependencies to the `build.gradle` of the module that will be published

```groovy
buildscript {
    repositories {
        jcenter()
        mavenCentral()
        maven { url 'http://dl.bintray.com/novoda/maven' }
    }
    dependencies {
        classpath 'com.jfrog.bintray.gradle:gradle-bintray-plugin:0.6'
        classpath 'com.novoda:gradle-android-release-plugin:0.0.3'
    }

}

apply plugin: 'gradle-android-release-plugin'
```

and add the publish configuration for the specific project:

```groovy
publish {
    userOrg = 'novoda'
    groupId = 'com.novoda'
    artifactId = 'artifact-name' // TODO: Use the proper artifact Id
    version = project.version
    uploadName = 'artifact-name' // This is the name that will be shown in bintray
    description = 'Oh hi, this is a nice description for a project right?' // TODO: Use right description
    website = ''https://github.com/novoda/blah...' // TODO: Use correct URL
    issueTracker = "${website}/issues"
    repository = "${website}.git"
}
```

Finally, use the command `publishReleaseToBintray` to publish, specifying the remaining parameters:
```
./gradlew publishReleaseToBintray -PbintrayUser=USERNAME -PbintrayKey=BINTRAY_KEY -PshouldUpload=true
```

