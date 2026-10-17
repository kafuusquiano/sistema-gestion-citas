# =====================================================================
# Imagen del backend. La construye Render en su nube: no hace falta
# tener Docker instalado en el equipo de desarrollo.
#
# Construccion en dos etapas. La primera compila con Maven y la segunda
# se queda solo con el JAR y un entorno de ejecucion de Java. Asi la
# imagen final no arrastra Maven ni el codigo fuente, lo que importa
# porque el plan gratuito de Render tiene 512 MB de memoria.
# =====================================================================

# ---------------------------------------------------------------------
# Etapa 1: compilacion
# ---------------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

# El pom.xml se copia solo, antes que el codigo, a proposito: Docker
# guarda en cache el resultado de cada instruccion y la descarga de
# dependencias es la parte lenta. Mientras el pom no cambie, un cambio
# en el codigo reutiliza esa capa en lugar de volver a descargarlo todo.
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

COPY src ./src

# Las pruebas no corren aqui sino en el flujo de integracion continua.
# Repetirlas en cada construccion de imagen alargaria el despliegue sin
# aportar informacion nueva.
RUN mvn -B -ntp clean package -DskipTests

# ---------------------------------------------------------------------
# Etapa 2: ejecucion
# ---------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Usuario sin privilegios. Si alguien lograra ejecutar codigo dentro del
# contenedor, no lo haria como root.
RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build /build/target/*.jar app.jar
RUN chown spring:spring app.jar

USER spring

EXPOSE 8080

# MaxRAMPercentage y no Xmx: le dice a la maquina virtual que use el 75%
# de la memoria que el contenedor tenga asignada, sea cual sea. Con un
# valor fijo, la JVM no se entera del limite de 512 MB de Render y el
# servicio muere por falta de memoria.
#
# UseSerialGC porque el plan gratuito da 0.1 CPU: un recolector paralelo
# dedicaria a coordinar hilos un tiempo que aqui no existe.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC -XX:TieredStopAtLevel=1"

# El puerto lo fija Render con la variable PORT, que application.yml lee
# en server.port con 8080 como valor por defecto.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
