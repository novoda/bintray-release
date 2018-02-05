This ia a separate gradle project that is kept in the same VCS repository.
If you want to checkout/use/test the samples - you should import this folder as a separate project.

It is a separate project because with gradle plugins - you cannot have a reference to your working code as a module. 
i.e. to test any changes you either have to make a release and depend on it, or like we are doing here have a second project 

In the `buildSrc` folder, we have a folder reference to the source of this plugin (`../../core/src/main/groovy`). 
This allows all modules in this folder to have the bintray-release plugin on their classpath at development time.

(For more about `buildSrc` [read this](https://zeroturnaround.com/rebellabs/using-buildsrc-for-custom-logic-in-gradle-builds/))

### Modules

#### android

This is an example project using the `bintray-release` plugin to release an Android library.

#### jvm

This is an example proect using the `bintray-release` plugin to release a Java library.
