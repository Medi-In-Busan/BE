# 배포용 이미지. 멀티스테이지로 빌드해서 런타임 이미지에는 JDK/Gradle이 남지 않게 한다.
# 로컬 개발(bootRun, H2)에는 이 파일이 필요 없다 — EC2 배포(도커허브 push → pull) 전용.

# ---- 1단계: bootJar 빌드 ----
# gradle:*-jdk21이 아니라 eclipse-temurin을 쓰는 이유: build.gradle의 Java 21 toolchain과
# 정확히 맞추고, Gradle 배포판은 프로젝트에 이미 포함된 gradlew(Gradle Wrapper)가 알아서
# 내려받으므로 베이스 이미지에 Gradle을 미리 넣어둘 필요가 없다.
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

# 의존성 캐시 레이어 분리: build.gradle/settings.gradle/gradlew(+wrapper)만 먼저 복사해
# 소스 코드(src/)만 바뀌었을 때는 이 레이어가 캐시에서 재사용되게 한다.
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew --version

COPY src ./src
# 테스트는 CI 파이프라인의 별도 단계(gradlew test)에서 이미 실행하므로 이미지 빌드에서는
# 건너뛴다 — 같은 검증을 두 번 하지 않고 이미지 빌드 시간을 줄인다.
RUN ./gradlew bootJar --no-daemon -x test

# ---- 2단계: 런타임 ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# 컨테이너 안에서도 root로 실행하지 않는다. WORKDIR 생성 시점엔 root 소유라, spring 유저로
# 전환하기 전에 반드시 /app 소유권을 넘겨야 한다 — 안 그러면 (prod 프로필이 아니어서) H2 파일
# DB 경로(./data, WORKDIR 기준 상대경로)를 만들 때 AccessDeniedException으로 기동 자체가 실패한다.
RUN groupadd --system spring && useradd --system --gid spring spring \
    && chown spring:spring /app
USER spring:spring

COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8080
# SPRING_PROFILES_ACTIVE=prod는 docker-compose.prod.yml에서 주입한다(application-prod.yml
# 참고) — 이 Dockerfile 자체는 프로필을 강제하지 않아, 필요하면 다른 프로필로도 그대로 쓸 수 있다.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
