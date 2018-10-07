#!/bin/sh

set -e

BASEDIR=$(dirname "$0")

# Testing the core plugin
cd $BASEDIR/../ && ./gradlew clean build bintrayUpload -PdryRun=true -PbintrayUser=user -PbintrayKey=key --info

# Testing the samples
cd $BASEDIR/../samples/ && ./gradlew clean build bintrayUpload -PdryRun=true -PbintrayUser=user -PbintrayKey=key --info
