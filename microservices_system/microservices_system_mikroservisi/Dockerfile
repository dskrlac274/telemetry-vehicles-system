FROM eclipse-temurin:21-jre

COPY ./*.txt /usr/app/
COPY ./src/main/resources/application.yaml /usr/app/

COPY ./target/dskrlac20_vjezba_08_dz_3_mikroservisi.jar /usr/app/
COPY ./target/libs /usr/app/libs

WORKDIR /usr/app

CMD java -jar ./dskrlac20_vjezba_08_dz_3_mikroservisi.jar
