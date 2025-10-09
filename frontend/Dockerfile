# 多阶段构建 - 构建阶段
FROM node:20-alpine AS builder

WORKDIR /app

# 复制 package.json 和 package-lock.json
COPY package*.json ./

# 安装依赖
RUN npm install

# 复制源代码
COPY . .

# 构建生产版本
RUN npm run build

# 运行阶段 - 使用 Nginx
FROM nginx:1.25-alpine

# 安装 gettext 包（提供 envsubst 命令）
RUN apk add --no-cache gettext

# 复制自定义 Nginx 配置模板
COPY nginx.conf /etc/nginx/templates/default.conf.template

# 从构建阶段复制构建产物
COPY --from=builder /app/dist /usr/share/nginx/html

# 更改所有权（nginx 用户已存在于基础镜像中）
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chown -R nginx:nginx /var/cache/nginx && \
    chown -R nginx:nginx /var/log/nginx && \
    chown -R nginx:nginx /etc/nginx/conf.d && \
    touch /var/run/nginx.pid && \
    chown -R nginx:nginx /var/run/nginx.pid

# 创建启动脚本（需要在切换用户前创建并设置权限）
RUN echo '#!/bin/sh' > /docker-entrypoint.sh && \
    echo 'set -e' >> /docker-entrypoint.sh && \
    echo 'echo "BACKEND_HOST is: ${BACKEND_HOST}"' >> /docker-entrypoint.sh && \
    echo 'envsubst "\$BACKEND_HOST" < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf' >> /docker-entrypoint.sh && \
    echo 'echo "Generated nginx config:"' >> /docker-entrypoint.sh && \
    echo 'cat /etc/nginx/conf.d/default.conf' >> /docker-entrypoint.sh && \
    echo 'exec nginx -g "daemon off;"' >> /docker-entrypoint.sh && \
    chmod +x /docker-entrypoint.sh

# 切换到非 root 用户
USER nginx

# 暴露端口
EXPOSE 80

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:80/ || exit 1

# 启动脚本
CMD ["/docker-entrypoint.sh"]
