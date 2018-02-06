#!/bin/sh

BASEDIR=$(dirname "$0")

function exitIfCommandFailed {
    if [ $1 -ne 0 ]
        then exit $1
    fi
}

# Testing the core plugin
cd $BASEDIR/../ && ./gradlew clean build bintrayUpload -PdryRun=true --info
exitIfCommandFailed $?

# Testing the samples
cd $BASEDIR/../samples/ && ./gradlew clean build bintrayUpload -PdryRun=true --info
exitIfCommandFailed $?
