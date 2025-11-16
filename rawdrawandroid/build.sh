#!/usr/bin/env bash

PROJECT_DIRECTORY=$PWD

# Run build:
pushd "${PROJECT_DIRECTORY}" || exit

# ANDROIDVERSION >=27
JENV_VERSION=1.8 make ANDROIDVERSION=27 ANDROIDTARGET=36

popd || exit

exit 0
