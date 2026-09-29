FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && \
    apt-get install -y \
    openjdk-17-jdk \
    maven \
    tesseract-ocr \
    tesseract-ocr-eng && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/5/tessdata

EXPOSE 8080

CMD ["sh", "-c", "java -jar target/*.jar --server.port=${PORT:-8080}"]