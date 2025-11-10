#!/usr/bin/env bash

export ANDROID_HOME=${HOME}/Android/api/16/android-sdk-linux
export PATH=$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools:$PATH

android update project -p . -s

exit 0
