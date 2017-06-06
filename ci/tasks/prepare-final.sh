#!/usr/bin/env bash
set -e

echo "running in prepare-final.sh"

version=`cat version/number`
echo "version to use: ${version}"

echo "Renaming release-candidate to final build: ${base_name}-${version}.jar"
cp bucket-release-candidate/${base_name}*.jar final-dir/${base_name}-${version}.jar
