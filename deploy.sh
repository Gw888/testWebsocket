#!/bin/bash

# 检查Docker是否已安装
if ! command -v docker &> /dev/null; then
    echo "正在安装Docker..."
    # 更新包索引
    apt-get update
    # 安装依赖
    apt-get install -y \
        apt-transport-https \
        ca-certificates \
        curl \
        gnupg \
        lsb-release

    # 添加Docker官方GPG密钥
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

    # 设置稳定版仓库
    echo \
    "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu \
    $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null

    # 安装Docker Engine
    apt-get update
    apt-get install -y docker-ce docker-ce-cli containerd.io
fi

# 停止并删除已存在的容器（如果存在）
if [ $(docker ps -a -q -f name=websocket-app) ]; then
    docker stop websocket-app
    docker rm websocket-app
fi

# 删除旧镜像（如果存在）
if [ $(docker images -q websocket-app) ]; then
    docker rmi websocket-app
fi

# 构建新镜像
docker build -t websocket-app .

# 运行新容器
docker run -d \
    --name websocket-app \
    -p 8080:8080 \
    --restart unless-stopped \
    websocket-app

echo "部署完成！应用已在8080端口运行"