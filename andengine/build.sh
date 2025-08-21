#!/usr/bin/env bash

NDK_DIRECTORY="${HOME}/Android/android-ndk-r9d"
PROJECT_DIRECTORY="$(dirname $(pwd))"

# Run build:
pushd ${PROJECT_DIRECTORY}
${NDK_DIRECTORY}/ndk-build V=1

# Clean temporary files:
rm -rf ${PROJECT_DIRECTORY}/obj
find . -name gdbserver -print0 | xargs -0 rm -rf
find . -name gdb.setup -print0 | xargs -0 rm -rf

popd

