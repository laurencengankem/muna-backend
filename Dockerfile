FROM amazoncorretto:11-alpine-jdk

RUN apk add --no-cache \
    freetype \
    libx11 \
    libxext \
    libxrender \
    libxtst \
    fontconfig

WORKDIR /app
COPY ./*.jar muna.jar
EXPOSE 9000
ENTRYPOINT ["java","-Djava.awt.headless=true","-jar","muna.jar"]
