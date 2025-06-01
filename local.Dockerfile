# ── 1단계: 빌드 스테이지 ──
FROM gradle:7.6-jdk17 AS builder

WORKDIR /home/gradle/project

# 1) 호스트의 프로젝트 전체를 복사 (일단 root 권한으로)
COPY . .

# 2) 복사된 파일/디렉터리를 전부 gradle:gradle 소유로 변경
RUN chown -R gradle:gradle /home/gradle/project

# 3) 이미 존재하는 build/ 디렉터리가 있다면(호스트에 남아 있을 경우) root 권한으로 삭제
USER root
RUN rm -rf /home/gradle/project/build

# 4) gradle 유저로 돌아와서 다시 전체를 gradle 소유로 변경
USER gradle
RUN chown -R gradle:gradle /home/gradle/project

# 5) Gradle 캐시 디렉터리 권한 설정
RUN mkdir -p /home/gradle/.gradle && chown -R gradle:gradle /home/gradle/.gradle

# 6) Gradle로 clean & bootJar 실행
RUN gradle clean bootJar --no-daemon

# ── 2단계: 런타임 스테이지 ──
FROM openjdk:17-slim

WORKDIR /app

# 1단계에서 생성된 JAR 파일만 복사
COPY --from=builder /home/gradle/project/build/libs/*.jar ./app.jar

# 애플리케이션 기동 (프로필 등은 docker-compose의 .env.local에서 주입)
ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar"]