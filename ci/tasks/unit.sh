#!/usr/bin/env bash
set -e

export ROOT_FOLDER=$( pwd )
export GRADLE_USER_HOME="${ROOT_FOLDER}/.gradle"
export GRADLE_OPTS=-Dorg.gradle.native=false

cd resource-git

gradle -v
gradle test
