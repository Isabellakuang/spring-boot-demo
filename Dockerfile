# 多阶段构建 - 构建阶段
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build

# 复制 Maven 包装器和 pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# 下载依赖（利用 Docker 缓存层）
RUN ./mvnw dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用
RUN ./mvnw clean package -DskipTests -B

# 运行阶段
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 添加 wget 用于健康检查
RUN apk add --no-cache wget

# 创建非 root 用户
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 从构建阶段复制 JAR
COPY --from=builder /build/target/demo-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# JVM 优化参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
