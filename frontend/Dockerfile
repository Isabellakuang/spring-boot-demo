# syntax=docker/dockerfile:1.6

# 可通过构建参数覆盖镜像前缀，默认走 DaoCloud 公共镜像
ARG REGISTRY_PREFIX=m.daocloud.io/docker.io/library

# -------------------------------
# Build stage
# -------------------------------
FROM ${REGISTRY_PREFIX}/node:20-alpine AS build
ARG REGISTRY_PREFIX

WORKDIR /app

# 仅复制依赖清单文件，便于缓存
COPY package*.json ./

# 安装依赖：优先使用 npm ci 保证可重复性，若无 lock 文件则回退到 npm install
# 针对 Alpine/musl 环境，删除 package-lock.json 并重新安装以解决 Rollup 可选依赖问题
RUN npm cache clean --force && \
    rm -f package-lock.json && \
    npm install --force

# 复制剩余源码并构建
COPY . .
RUN npm run build && rm -rf node_modules

# -------------------------------
# Production stage
# -------------------------------
ARG REGISTRY_PREFIX=m.daocloud.io/docker.io/library
FROM ${REGISTRY_PREFIX}/nginx:alpine
ARG REGISTRY_PREFIX

# 拷贝构建产物到 nginx 默认站点目录
COPY --from=build /app/dist /usr/share/nginx/html

# 拷贝自定义 nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 可选：设置健康检查，确保服务端点可用
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
    CMD wget -q -O- http://localhost/ || exit 1

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
