# bintray-release [![](https://ci.novoda.com/buildStatus/icon?job=bintray-release)](https://ci.novoda.com/job/bintray-release/lastBuild/console) [![](https://raw.githubusercontent.com/novoda/novoda/master/assets/btn_apache_lisence.png)](LICENSE.txt)

Super duper easy way to release your Android and other artifacts to bintray.


## Description

This is a helper for releasing libraries to bintray. It is intended to help configuring stuff related to maven and bintray.
At the moment it works with Android Library projects, plain Java and plain Groovy projects, but our focus is to mainly support Android projects.


## Adding to project

To publish a library to bintray using this plugin, add these dependencies to the `build.gradle` of the module that will be published:

```groovy
apply plugin: 'com.novoda.bintray-release' // must be applied after your artifact generating plugin (eg. java / com.android.library)

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.novoda:bintray-release:0.3.5'
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
    publishVersion = '0.3.4'
    desc = 'Oh hi, this is a nice description for a project, right?'
    website = 'https://github.com/novoda/bintray-release'
}
```

Finally, use the task `bintrayUpload` to publish (make sure you build the project first!):

```bash
$ ./gradlew clean build bintrayUpload -PbintrayUser=BINTRAY_USERNAME -PbintrayKey=BINTRAY_KEY -PdryRun=false
```

More info on the available properties and other usages in the [Github Wiki](https://github.com/novoda/bintray-release/wiki).

## Add support for syncing to maven central

As jCenter has superceeded Maven Central as the most popular repository for your dependencies, this plugin doesn't provide full support to sync between bintray and Maven Central out of the box (you can read the full discussion [here](https://github.com/novoda/bintray-release/issues/19)).
Adding missing fields in the generated pom-file is pretty easy though.

In addition to the steps described above, do the following:

1. Open your project's `build.gradle`.
2. Define some extended properties like this and replace the placeholders with your own values. They will be used in the hook that will a generate pom-file that won't be rejected by Maven Central. `IS_UPLOADING` will be true if you invoke `./gradlew bintrayUpload`.

```groovy
ext {
    ARTIFACT_ID = 'YOUR_ARTIFACT_ID'
    VERSION_NAME = 'YOUR_VERSION_NAME'
    VERSION_CODE = 1 //your version
    
    DESCRIPTION = 'YOUR_DESCRIPTION'
    
    SITE_URL = 'YOUR_SITE_URL'
    GIT_URL = 'YOUR_GIT_URL'
    GROUP_NAME = 'YOUR_GROUP_NAME'
    COMPILE_SDK = 23
    BUILD_TOOLS = '23.0.1'
    
    MODULE_NAME = 'YOUR_MODULE_NAME'
    
    LICENSE = 'YOUR_LICENSE'
    
    DEVELOPER_ID = 'YOUR_DEVELOPER_ID'
    DEVELOPER_NAME = 'YOUR_NAME'
    DEVELOPER_EMAIL = 'YOUR_EMAIL_ADDRESS'
    
    IS_UPLOADING = project.getGradle().startParameter.taskNames.any{it.contains('bintrayUpload')}}
}
```
Feel free to define those properties somewhere else. They tend to be pretty general in nature, you will probably already have a place where you define your properties. 

3. Add the hook that will first delete the pom-file and regenerate one that meets Maven Central's criteria. If you want to apply the hock to several modules in your project, make sure to specify them all in the if-clause.

```groovy
subprojects {
    group = GROUP_NAME
    version = VERSION

    if (IS_UPLOADING && project.name in [MODULE_NAME]) {
        println project.name
        apply plugin: 'maven'

        gradle.taskGraph.whenReady { taskGraph ->
            taskGraph.getAllTasks().find {
                it.path == ":$project.name:generatePomFileForMavenPublication"
            }.doLast {
                file("build/publications/maven/pom-default.xml").delete()
                println 'Overriding pom-file to make sure we can sync to maven central!'
                pom {
                    //noinspection GroovyAssignabilityCheck
                    project {
                        name "$project.name"
                        artifactId ARTIFACT_ID
                        packaging project.name == 'compiler' ? 'jar' : 'aar'
                        description DESCRIPTION
                        url SITE_URL
                        version VERSION_NAME

                        scm {
                            url GIT_URL
                            connection GIT_URL
                            developerConnection GIT_URL
                        }

                        licenses {
                            license {
                                name LICENSE
                            }
                        }

                        developers {
                            developer {
                                id DEVELOPER_ID
                                name DEVELOPER_NAME
                                email DEVELOPER_EMAIL
                            }
                        }
                    }
                }.writeTo("build/publications/maven/pom-default.xml")
            }
        }
    }
}
```

## Links

Here are a list of useful links:

 * We always welcome people to contribute new features or bug fixes, [here is how](https://github.com/novoda/novoda/blob/master/CONTRIBUTING.md)
 * If you have a problem check the [Issues Page](https://github.com/novoda/bintray-release/issues) first to see if we are working on it
 * For further usage or to delve more deeply checkout the [Project Wiki](https://github.com/novoda/bintray-release/wiki)
 * Looking for community help, browse the already asked [Stack Overflow Questions](http://stackoverflow.com/questions/tagged/support-bintray-release) or use the tag: `support-bintray-release` when posting a new question  
