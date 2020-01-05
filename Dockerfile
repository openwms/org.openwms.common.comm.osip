FROM azul/zulu-openjdk-alpine:11-jre
VOLUME /tmp
ARG JAVA_OPTS="-noverify -Xmx250m -Xss512k"
ADD target/tcpip-driver.jar app.jar
ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar
