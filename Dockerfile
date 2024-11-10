#BUILD
FROM maven:3.8.6-openjdk-18 as build

WORKDIR /app
COPY . .
RUN mvn install -DskipTests=true

#RUN STAGE
FROM amazoncorretto:18-alpine

WORKDIR /run
COPY --from=build /app/target/roms-0.0.1-SNAPSHOT.jar /run/application.jar

EXPOSE 9999
ENTRYPOINT ["java","-jar","/run/application.jar"]
