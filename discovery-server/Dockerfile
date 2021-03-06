#### Start of builder image
# ------------------------
# Builder stage to prepare application for final image
FROM openjdk:15-slim-buster as builder
WORKDIR temp

# Fatjar location
ARG JAR_FILE=build/libs/*.jar

# Copy fat jar file to current image builder
COPY ${JAR_FILE} application.jar

# Extract the jar file layers
RUN java -Djarmode=layertools -jar --enable-preview application.jar extract
#### End of builder stage

#### Start of actual image
# ------------------------
FROM openjdk:15-slim-buster

# Set image information, but could be set to different location from command line
ARG IMAGE_VERSION="0.0.1-SNAPSHOT"
ARG IMAGE_NAME="Discovery Service"
ARG MAINTAINER="Rajab Davudov <radjab@gmail.com>"

LABEL version=${IMAGE_VERSION} name=${IMAGE_NAME} maintainer=${MAINTAINER}

# Limiting security access to not user root user
RUN addgroup appgroup && useradd -g appgroup -ms /bin/bash posting

# Setting user to current created user
USER posting

# Set working directory to application folder
WORKDIR /home/posting/application

# Copy all layers from builder stage to current image
COPY --from=builder temp/dependencies/ ./
COPY --from=builder temp/snapshot-dependencies/ ./
COPY --from=builder temp/spring-boot-loader/ ./
COPY --from=builder temp/application/ ./


# Expose current application to port 8761
EXPOSE 8761

ARG JAVA_OPTS=""

# Run the application with JVM configs if any
ENTRYPOINT ["bash", "-c", \
"java -server --enable-preview -XX:+UseContainerSupport -XX:+ShowCodeDetailsInExceptionMessages \
-XX:+AlwaysActAsServerClassMachine -XX:+UseG1GC -XX:+UseStringDeduplication ${JAVA_OPTS} \
org.springframework.boot.loader.JarLauncher ${0} ${@}"]
#### End of actual stage
