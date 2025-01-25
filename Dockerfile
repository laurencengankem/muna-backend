# Use OpenJDK 11 slim as base image
FROM openjdk:11-slim

# Install missing dependencies including freetype
RUN apt-get update && apt-get install -y \
    libfreetype6 \
    libx11-dev \
    libxext-dev \
    libxrender-dev \
    libxtst-dev \
    fontconfig && \
    rm -rf /var/lib/apt/lists/*

# Set the working directory
WORKDIR .

# Add your JAR file to the container
ADD ./*.jar muna.jar

# Expose the port your app will run on
EXPOSE 9000

# Run the Java application
ENTRYPOINT ["java","-Djava.awt.headless=true", "-jar", "muna.jar"]
