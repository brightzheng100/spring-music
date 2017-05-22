#!/usr/bin/env bash
set -x

export TERM=${TERM:-dumb}

target="cf api $API_ENDPOINT --skip-ssl-validation"
echo $target
eval $target

echo "Login....."
login="cf auth $USERNAME $PASSWORD"
#echo $login
eval $login

echo "Target Org and Space"
org_space="cf target -o $ORG -s $SPACE"
eval $org_space

echo "CD to the project folder"
cd resource-repo

echo "Build Jar by Gradle"
./gradlew build

echo "push the app"
push="cf push"
#echo $push
eval $push
