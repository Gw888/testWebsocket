#!/bin/bash

# 停止并删除旧容器
docker stop websocket-app || true
docker rm websocket-app || true

# 删除旧镜像
docker rmi websocket-app:latest || true

# 构建新镜像
docker build -t websocket-app:latest .

# 运行新容器
docker run -d --name websocket-app -p 8080:8080 websocket-app:latest 