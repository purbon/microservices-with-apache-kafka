FROM anapsix/alpine-java:8_jdk
MAINTAINER pere.urbon@acm.org

# sbt

ENV SBT_URL=https://dl.bintray.com/sbt/native-packages/sbt
ENV SBT_VERSION 0.13.15
ENV INSTALL_DIR /usr/local
ENV SBT_HOME /usr/local/sbt
ENV PATH ${PATH}:${SBT_HOME}/bin

# Install sbt
#RUN apk add --no-cache --update bash wget && mkdir -p "$SBT_HOME" && \
#    wget -qO - --no-check-certificate "https://dl.bintray.com/sbt/native-packages/sbt/$SBT_VERSION/sbt-$SBT_VERSION.tgz" |  tar xz -C $INSTALL_DIR && \
#    echo -ne "- with sbt $SBT_VERSION\n" >> /root/.built

# Install git
RUN  apk add --no-cache git openssh

# Install node.js
#RUN apk add nodejs

# Copy play project and compile it
# This will download all the ivy2 and sbt dependencies and install them
# in the container /root directory

ENV PROJECT_HOME /usr/src
ENV PROJECT_NAME payments-app

#COPY ${PROJECT_NAME} ${PROJECT_HOME}/${PROJECT_NAME}
#RUN cd $PROJECT_HOME/$PROJECT_NAME && \
#    sbt compile

# Command

ENV ARTEFACT target/universal/payments-app-1.0-SNAPSHOT.zip
COPY ${PROJECT_NAME}/${ARTEFACT} ${PROJECT_HOME}/${PROJECT_NAME}/payments-app-1.0-SNAPSHOT.zip
RUN cd $PROJECT_HOME/$PROJECT_NAME && unzip payments-app-1.0-SNAPSHOT.zip


# Expose code volume and play port 9000

EXPOSE 9000
#VOLUME "payments-app"
WORKDIR $PROJECT_HOME/$PROJECT_NAME/payments-app-1.0-SNAPSHOT

ENV APPLICATION_SECRET Q%X57W7Efy
ENV PATH="$PROJECT_HOME/$PROJECT_NAME/payments-app-1.0-SNAPSHOT:${PATH}"

CMD ["bin/payments-app"]
