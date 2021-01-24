FROM azul/zulu-openjdk-alpine:11

COPY "build/libs/order-manager-0.0.1.jar" "/etc/app.jar"

ENTRYPOINT ["java", "-Dspring.profiles.active=dev","-jar","/etc/app.jar"]
