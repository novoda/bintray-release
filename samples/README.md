This is a separate gradle project that is kept in the same VCS repository.
If you want to checkout/use/test the samples - you should import this folder as a separate project.

It is a separate project because gradle plugins need to be included as buildscript dependencies of the project using
them, and this requires the use of a compiled artifact of the plugin.

In order to streamline this process and avoid to play with manual local/remote releases we are making the `samples`
project to compile its own local version of the plugin directly from its sources, leveraging the Gradle composite builds support.
This allow us to instruct the `samples` project to resolve the `com.novoda:bintray-release:*` dependency using the output 
of the plugin Gradle project.
(For more info about composite builds [read this](https://docs.gradle.org/current/userguide/composite_builds.html))

### Modules

#### android

This is an example project using the `bintray-release` plugin to release an Android library.

#### jvm

This is an example proect using the `bintray-release` plugin to release a Java library.
