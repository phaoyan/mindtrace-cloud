FROM openjdk:18-jdk-slim
WORKDIR /app
COPY mindtrace-gateway/target/mindtrace-gateway-1.0-SNAPSHOT.jar /app/mindtrace-gateway.jar
COPY mindtrace-core/target/mindtrace-core-1.0-SNAPSHOT.jar /app/mindtrace-core.jar
COPY mindtrace-enhancer/target/mindtrace-enhancer-1.0-SNAPSHOT.jar /app/mindtrace-enhancer.jar
COPY mindtrace-share/target/mindtrace-share-1.0-SNAPSHOT.jar /app/mindtrace-share.jar
COPY mindtrace-tracing/target/mindtrace-tracing-1.0-SNAPSHOT.jar /app/mindtrace-tracing.jar
COPY mindtrace-mastery/target/mindtrace-mastery-1.0-SNAPSHOT.jar /app/mindtrace-mastery.jar
COPY mindtrace-hub/target/mindtrace-hub-1.0-SNAPSHOT.jar /app/mindtrace-hub.jar
COPY mindtrace-local/target/mindtrace-local-1.0-SNAPSHOT.jar /app/mindtrace-local.jar
COPY mindtrace-path-optimizer/target/mindtrace-path-optimizer-1.0-SNAPSHOT.jar /app/mindtrace-path-optimizer.jar
COPY mindtrace-recommend/target/mindtrace-recommend-1.0-SNAPSHOT.jar /app/mindtrace-recommend.jar
COPY startup.sh /app/startup.sh
EXPOSE 34443
CMD ["sh","startup.sh"]
