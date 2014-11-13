# jarjaar-binks [![](http://ci.novoda.com/buildStatus/icon?job=jarjaar-binks)](http://ci.novoda.com/job/jarjaar-binks/lastSuccessfulBuild/console) [![](https://raw.githubusercontent.com/novoda/novoda/master/assets/btn_apache_lisence.png)](LICENSE.txt)

Super duper easy way to release your Android and other artifacts to bintray.


## Description

This is a helper for releasing libraries to bintray. It is intended to help configuring stuff related to maven and bintray.
At the moment it works with Android Library projects, plain Java and plain Groovy projects, but our focus is to mainly support Android projects.


## Adding to project

To publish a library to bintray using this plugin, add these dependencies to the `build.gradle` of the module that will be published:

```groovy
apply plugin: 'jarjaar-binks' // must be applied after your artifact generating plugin (eg. java / com.android.library)

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.novoda:jarjaar-binks:0.2.4'
    }
}
```


## Simple usage

Use the `publish` closure to set the info of your package:

```groovy
publish {
    userOrg = 'novoda'
    groupId = 'com.novoda'
    artifactId = 'jarjaar-binks'
    version = '0.2.4'
    description = 'Oh hi, this is a nice description for a project right?'
    website = 'https://github.com/novoda/jarjaar-binkse'
}
```

Finally, use the task `bintrayUpload` to publish (make sure you build the project first!):

```bash
$ ./gradlew clean build bintrayUpload -PbintrayUser=BINTRAY_USERNAME -PbintrayKey=BINTRAY_KEY -PdryRun=false
```

More info on the available properties and other usages in the [Github Wiki](https://github.com/novoda/jarjaar-binks/wiki).


## Links

Here are a list of useful links:

 * [Contributing](https://github.com/novoda/novoda/blob/master/CONTRIBUTING.md)
 * [Github Issues](https://github.com/novoda/jarjaar-binks/issues)
 * [Github Wiki](https://github.com/novoda/jarjaar-binks/wiki)
 * Stack Overflow Tag: `support-jarjaar`
