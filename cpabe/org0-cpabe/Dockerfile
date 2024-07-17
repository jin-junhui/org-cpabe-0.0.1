FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y maven
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
RUN mvn clean package
COPY libs/cpabe-api-1.0.2.jar /usr/src/myapp/target/dependency/
CMD ["java", "-cp", "target/dependency/*:target/org0-cpabe-0.0.1-SNAPSHOT.jar", "com.example.AACServer"]
