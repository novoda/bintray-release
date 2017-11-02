# bintray-release [![](https://ci.novoda.com/buildStatus/icon?job=bintray-release)](https://ci.novoda.com/job/bintray-release/lastBuild/console) [![Download](https://api.bintray.com/packages/novoda/maven/bintray-release/images/download.svg) ](https://bintray.com/novoda/maven/bintray-release/_latestVersion) [![](https://raw.githubusercontent.com/novoda/novoda/master/assets/btn_apache_lisence.png)](LICENSE.txt)

bintray-release提供了将您的Android或者其他作品推送到Bintray的简便方案。


## 描述

这是帮助将类库推送到bintray的助手。 它用来帮助配置与maven和bintray建立联系。
目前它用于Android Library工程，Java项目以及Groovy项目，但我们的重点主要是用来支持Android项目。


## 添加到工程

要使用此插件将库发布到bintray，请将以下这些依赖项添加到将要发布的模块的“build.gradle”中：

```groovy
apply plugin: 'com.novoda.bintray-release' // must be applied after your artifact generating plugin (eg. java / com.android.library)

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.novoda:bintray-release:<latest-version>'
    }
}
```


## 简单示例

使用`publish`闭包来设置你的应用的信息：

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

最后，使用任务`bintrayUpload`发布（需要先编译项目！）：

```bash
$ ./gradlew clean build bintrayUpload -PbintrayUser=BINTRAY_USERNAME -PbintrayKey=BINTRAY_KEY -PdryRun=false