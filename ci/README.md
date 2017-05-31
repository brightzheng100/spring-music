# Spring music with [Concourse](http://councourse.ci) Pipeline

![](images/pipeline.png)

## Resources

Resources in Concourse are implemented as docker images which contain implementations corresponding their types. In this pipeline, we use below resources:

- [git-resource](https://github.com/concourse/git-resource): named `git-repo` in the pipeline, which is where Spring Music App is hosted, be it in on-prem or in Github.

- [semver-resource](https://github.com/concourse/semver-resource): named `version` in the pipeline, which uses a version file, stored in AWS S3 or S3 compatible (e.g [Minio](https://www.minio.io/)) bucket, to track the version, e.g. 1.0.1, for artifacts versioning and tagging for git repo.

- [s3-resource](https://github.com/concourse/s3-resource): named `bucket-release-candidate` and `bucket-production-release` in the pipeline, which define the buckets to store release candidates (e.g. spring-music-1.0.1-rc.1.jar) and final release artifacts (e.g spring-music-1.0.1.jar).

- [cf-resource](https://github.com/concourse/cf-resource): named `cf-deploy-test-app`, `cf-deploy-uat-app` and `cf-deploy-production-app` in the pipeline, to represent 3 different Cloud Foundry environments  


## Pipeline

There are 6 jobs in this pipeline:
- Unit Test
- Build Artifacts
- Integration Tests
- Promote to UAT
- Ship It to Production
- Tag It

### 1. Unit Test

This job has two tasks:
* checks out the source from `git-repo`;
* runs a task to perform unit test simply by `gradle test` command.


### 2. Build Artifacts

This job has 6 tasks:
* checks out the source from `git-repo`;
* checks out the current version to get the right RC version;
* builds the apps by `gradle assemble -Pversion=$version`
* puts the built artifact, e.g spring-music-1.0.2-rc.1.jar to S3 bucket defined as resource `bucket-release-candidate`
* (optional, uncomment it to enable) tags Git repo as release candidate, e.g. 1.0.2-rc.2
* bumps the `version` resource for the next usage

### 3. Integration Test

This job has 3 tasks:
* aggregates `version`, `bucket-release-candidate` and `git-repo` tasks to make sure that all inputs are ready before processing;
* deploys to Cloud Foundry SIT env defined by resource `cf-deploy-test-app`, where we can specify org and space;
* verifies the deployment. Here we simply curl the app's home page to check wether we can get 200 HTTP response.

### 4. Promote To UAT

Similar to above step of "Integration Test", this job:
* aggregates `version`, `bucket-release-candidate` and `git-repo` tasks to make sure that all inputs are ready before processing;
* deploys to Cloud Foundry UAT env defined by resource `cf-deploy-uat-app`, where we can specify org and space
* then waits for user acceptance tests

### 5. Ship It To Production

This job is intentionally set as manual one. There hasn't any enabled triggers as job's input. So after UAT, there is a need to trigger it manually for further processing:
* aggregates `version`, `bucket-release-candidate` and `git-repo` tasks;
* prepares the artifact with right release version, e.g spring-music-1.0.2.jar;
* puts it to `bucket-production-release`
* deploys to Cloud Foundry PROD env defined by resource `cf-deploy-production-app`, where we can specify org and space

### 6. Tag It To Git Repo

This job has 3 tasks:
* retrieves back the current release `version`
* tags it to `git-repo`
* bumps the `version` resource for the next usage


## How to replicate this pipeline in your env

* If you don't already have a Concourse environment, you can quickly spin one up locally with [Vagrant](https://concourse.ci/vagrant.html)

* Download the `fly` CLI by visiting `http://192.168.100.4:8080` and selecting your OS then install

* Fork this Github repo to your own Github account, [ generate the key pair and add the public key to github ](https://help.github.com/articles/generating-ssh-keys/), and save the private key for future usage.

* If you don't have AWS account or simply don't want to use AWS S3, you can install [Minio](https://www.minio.io/) locally, it's S3 compatible. Then prepare two buckets:
  * spring-music-rc
  * spring-music-release

* Install [PCF Dev](http://pivotal.io/pcf-dev) or simply register a free trail account in [Pivotal Web Services](https://pws.pivotal.io/) and then:
  * Setup spaces for test, uat and prod by:
  * `cf create-space test`
  * `cf create-space uat`
  * `cf create-space prod`


* Set your fly endpoint (assuming you are taking the easy way and using vagrant)

  * `fly -t lite login -c http://192.168.100.4:8080`

* Create a new `parameters.yml` file by following below example:

```
VERIFY_URL: [CHANGE-ME], e.g https://spring-music-test-bright-zheng.cfapps.io/

CF_API: https://api.run.pivotal.io
CF_USER: [CHANGE-ME]
CF_PASS: [CHANGE-ME]
CF_TEST_ORG: [CHANGE-ME]
CF_TEST_SPACE: test
CF_UAT_ORG: [CHANGE-ME]
CF_UAT_SPACE: uat
CF_PROD_ORG: XXX
CF_PROD_SPACE: prod

S3_BUCKET_RC: spring-music-rc
S3_BUCKET_RELEASE: spring-music-release
S3_RC_REGEXP: spring-music-(.*).(.*).(.*)-rc.(.*).jar
S3_PROD_REGEXP: spring-music-(.*).(.*).(.*).jar
S3_VERSION_FILE: version
S3_ACCESS_KEY_ID: [CHANGE-ME]
S3_SECRET_ACCESS_KEY: [CHANGE-ME]
S3_ENDPOINT: http://192.168.100.4:9000

GIT_REPO: [CHANGE-ME], e.g git@github.com:brightzheng100/spring-music.git
GIT_PRIVATE_KEY: |
  -----BEGIN RSA PRIVATE KEY-----
  PUT YOUR GITHUB ACCOUNT PRIVATE KEY
  -----END RSA PRIVATE KEY-----

```

  * Set up your pipeline
    * `fly -t lite set-pipeline -p spring-music-pipeline -c spring-music/ci/pipeline.yml -l parameters.yml`
    * `fly -t lite unpause-pipeline -p spring-music-pipeline`
    * Open `http://192.168.100.4:8080` in your browser and enjoy!
