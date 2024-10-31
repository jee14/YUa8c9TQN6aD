FROM amazoncorretto:21 as gradle-builder

WORKDIR /builder
COPY gradlew build.gradle.kts settings.gradle.kts /builder/
RUN ./gradlew build -x test --parallel --continue > /dev/null 2>&1 || true

COPY . /builder/
RUN ./gradlew bootJar --no-daemon --stacktrace
RUN cp ./build/libs/YUa8c9TQN6aD-0.1.jar app.jar

FROM amazoncorretto:21

ARG OPEN_AI_KEY

WORKDIR /was
COPY --from=gradle-builder /builder/app.jar .

EXPOSE 8080
ENV TZ="Asia/Seoul"
ENV OPENAI_API_KEY=${OPENAI_API_KEY}

CMD java \
    -jar app.jar
