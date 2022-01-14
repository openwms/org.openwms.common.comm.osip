FROM openjdk:17.0.1-oracle
ARG JAVA_OPTS="-Xss512k"
ADD target/tcpip-driver-exec.jar app.jar
ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar
