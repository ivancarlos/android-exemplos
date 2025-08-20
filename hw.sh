#!/usr/bin/env bash

#export ANDROID_HOME=/home/ivan/Android/api/16/android-sdk-linux
#export PATH=$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools:$PATH

[ "$ANDROID_HOME" ] || {
	echo defina \$ANDROID_HOME
	exit 2
}

# id: 2 or "android-16"
#      Name: Android 4.1.2
#      Type: Platform
#      API level: 16
#      Revision: 5
#      Skins: HVGA, QVGA, WQVGA400, WQVGA432, WSVGA, WVGA800 (default),
#      WVGA854, WXGA720, WXGA800, WXGA800-7in
#  Tag/ABIs : default/armeabi-v7a, default/x86

android create project \
	--target 2 \
	--name MyApp \
	--path $PWD/MyApp \
	--activity MainActivity \
	--package com.example.myapp

exit 0

# comando que usei após baixar sdk
# android update sdk -u
