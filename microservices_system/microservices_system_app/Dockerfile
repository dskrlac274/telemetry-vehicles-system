FROM eclipse-temurin:21-jre

COPY ./*.txt /usr/app/
COPY ./*.csv /usr/app/

COPY ./sveZaustavi.sh /usr/app/
COPY ./zaustaviJVMProces.sh /usr/app/
COPY ./pokreniVarijante.sh /usr/app/

COPY ./target/dskrlac20_vjezba_08_dz_3_app-1.2.0.jar /usr/app/
COPY ./target/libs /usr/app/libs

WORKDIR /usr/app

RUN chmod +x ./sveZaustavi.sh ./zaustaviJVMProces.sh ./pokreniVarijante.sh

EXPOSE 8080

CMD ["./pokreniVarijante.sh"]
