###############################################################################
# Copyright 2016-2017 Dell Inc.
# Copyright 2018 Dell Technologies, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
###############################################################################
# Docker image for Alerts micro service
# FROM java:8
FROM maven:3.3-jdk-8-alpine

ARG MVN_COMMAND="mvn dependency:copy -q"

COPY docker-files/pom.xml .

RUN $MVN_COMMAND

FROM alpine:3.6
MAINTAINER Burning <burning9699@Gmail.com>

RUN apk --update add openjdk8-jre

# environment variables
ENV APP_DIR=/edgex/edgex-support-alerts
ENV TEMPLATE_DIR=/edgex/edgex-support-alerts/templates
ENV APP=support-alerts.jar
ENV APP_PORT=47001

#copy JAR and property files to the image
COPY --from=0 *.jar $APP_DIR/$APP
COPY docker-files/*.properties $APP_DIR/
#copy drool template to templates location
COPY docker-files/*.drl $TEMPLATE_DIR/

RUN mkdir /edgex/edgex-support-alerts/rules
RUN echo "this directory is reserved for EdgeX Foundry Drools rule files" > /edgex/edgex-support-alerts/rules/README

#expose support alerts port
EXPOSE $APP_PORT

#set the working directory
WORKDIR $APP_DIR

#kick off the micro service
ENTRYPOINT java -jar -Djava.security.egd=file:/dev/urandom -Xmx100M $APP