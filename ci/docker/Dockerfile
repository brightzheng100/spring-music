# Commands:
# - $ docker build . -t javagradlegitpack:latest
# - $ docker tag javagradlegitpack itstarting/javagradlegitpack
# - $ docker push itstarting/javagradlegitpack

FROM openjdk:8-jdk-alpine
MAINTAINER Bright Zheng "bright.zheng@outlook.com"

CMD ["gradle"]

ENV GRADLE_HOME /opt/gradle
ENV GRADLE_VERSION 2.14
ENV GRADLE_USER_HOME /home/gradle/.gradle

ARG GRADLE_DOWNLOAD_SHA256=993b4f33b652c689e9721917d8e021cab6bbd3eae81b39ab2fd46fdb19a928d5
RUN set -o errexit -o nounset \
	&& echo "==>Installing build dependencies" \
	&& apk add --no-cache --virtual .build-deps \
		ca-certificates \
		openssl \
		unzip \
	\
	&& echo "==>Downloading Gradle" \
	&& wget -O gradle.zip "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
	\
	&& echo "==>Checking download hash" \
	&& echo "${GRADLE_DOWNLOAD_SHA256} *gradle.zip" | sha256sum -c - \
	\
	&& echo "==>Installing Gradle" \
	&& unzip gradle.zip \
	&& rm gradle.zip \
	&& mkdir /opt \
	&& mv "gradle-${GRADLE_VERSION}" "${GRADLE_HOME}/" \
	&& ln -s "${GRADLE_HOME}/bin/gradle" /usr/bin/gradle \
	\
	&& apk del .build-deps \
	\
	&& echo "==>Adding gradle user and group" \
	&& addgroup -S -g 1000 gradle \
	&& adduser -D -S -G gradle -u 1000 -s /bin/ash gradle \
	&& mkdir /home/gradle/.gradle \
	&& chown -R gradle:gradle /home/gradle \
	\
	&& echo "==>Symlinking root Gradle cache to gradle Gradle cache" \
	&& ln -s /home/gradle/.gradle /root/.gradle \
  \
  && echo "==>Adding Git" \
  && apk --update add git openssh \
  \
  && echo "==>Adding necessary tools: bash, curl" \
  && apk --update add bash curl\
	\
  && rm -rf /var/lib/apt/lists/* \
  && rm /var/cache/apk/*


# Create Gradle volume
#USER gradle
VOLUME "/home/gradle/.gradle"
WORKDIR /home/gradle

RUN set -o errexit -o nounset \
	&& echo "==>Testing Gradle and Git Installation" \
	&& gradle --version \
    && git version