# 构建一个新的镜像，仅包含运行时环境
FROM openjdk:18-jdk-slim
# 设置工作目录
WORKDIR /app
# 从上一层镜像中复制构建好的应用文件
COPY target/mindtrace-mastery-1.0-SNAPSHOT.jar /app
# 暴露端口，根据实际项目情况设置
EXPOSE 33816
# 设置容器的默认启动命令
CMD ["java", "-jar", "mindtrace-mastery-1.0-SNAPSHOT.jar"]