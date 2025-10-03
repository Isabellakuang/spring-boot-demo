# 多阶段构建 - 构建阶段
# 使用阿里云镜像加速
FROM docker.m.daocloud.io/library/maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build

# 配置 Maven 使用阿里云镜像仓库加速依赖下载
RUN mkdir -p /root/.m2 && \
    echo '<?xml version="1.0" encoding="UTF-8"?>' > /root/.m2/settings.xml && \
    echo '<settings>' >> /root/.m2/settings.xml && \
    echo '  <mirrors>' >> /root/.m2/settings.xml && \
    echo '    <mirror>' >> /root/.m2/settings.xml && \
    echo '      <id>aliyun-maven</id>' >> /root/.m2/settings.xml && \
    echo '      <mirrorOf>central</mirrorOf>' >> /root/.m2/settings.xml && \
    echo '      <name>Aliyun Maven</name>' >> /root/.m2/settings.xml && \
    echo '      <url>https://maven.aliyun.com/repository/public</url>' >> /root/.m2/settings.xml && \
    echo '    </mirror>' >> /root/.m2/settings.xml && \
    echo '  </mirrors>' >> /root/.m2/settings.xml && \
    echo '</settings>' >> /root/.m2/settings.xml

# 复制 pom.xml
COPY pom.xml ./

# 下载依赖（利用 Docker 缓存层）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests -B

# 运行阶段 - 使用 docker-0.unsee.tech 镜像
FROM docker.m.daocloud.io/library/openjdk:21-jdk-slim
WORKDIR /app

# 创建非 root 用户
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# 从构建阶段复制 JAR
COPY --from=builder /build/target/demo-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# JVM 优化参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
