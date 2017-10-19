#!/usr/bin/env bash
set -e

export GRADLE_OPTS=-Dorg.gradle.native=false
version="1.0.0"
echo "version to use: ${version}"

cd resource-git
gradle assemble -Pversion=$version
cp build/libs/*.jar ../artifact-dir/
