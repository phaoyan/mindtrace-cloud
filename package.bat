cd ./mindtrace-gateway
call mvn clean -f pom.xml
call mvn package -f pom.xml
cd ../mindtrace-core
call mvn clean -f pom.xml
call mvn package -f pom.xml
cd ../mindtrace-enhancer
call mvn clean -f pom.xml
call mvn package -f pom.xml
cd ../mindtrace-hub
call mvn clean -f pom.xml
call mvn package -f pom.xml
cd ../mindtrace-mastery
call mvn clean -f pom.xml
call mvn package -f pom.xml
cd ../mindtrace-tracing
call mvn clean -f pom.xml
call mvn package -f pom.xml
cd ../mindtrace-share
call mvn clean -f pom.xml
call mvn package -f pom.xml
cd ../mindtrace-local
call mvn clean -f pom.xml
call mvn package -f pom.xml
cd ../mindtrace-path-optimizer
call mvn clean -f pom.xml
call mvn package -f pom.xml
cd ../mindtrace-recommend
call mvn clean -f pom.xml
call mvn package -f pom.xml
