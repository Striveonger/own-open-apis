#!/bin/bash
set -x

# select arch in "amd64" "arm64"; do
#     break;
# done

# echo $arch

# 获取当前脚本的上一级目录的绝对路径, 并将其赋值给 APP_WORKSHOP 变量
APP_WORKSHOP=$(realpath "$(dirname "$0")/..")

pushd "${APP_WORKSHOP}" || exit

# build service
mvn -f internal/service/pom.xml clean package -DskipTests -P k8s

# --------------------------------------------------------------------------------------------
# uninstall helm
helm uninstall own-open-apis -n own
# kubectl delete ns own
# remove image
docker rmi striveonger/own-open-apis:$(cat ./ci-cd/VERSION)

# --------------------------------------------------------------------------------------------
# build image
docker build -f ./ci-cd/docker/Dockerfile -t striveonger/own-open-apis:$(cat ./ci-cd/VERSION) .

# docker push docker.io/striveonger/own-open-apis:$(cat ./ci-cd/VERSION)

# package helm
helm package ci-cd/helm
mv own-open-apis-$(cat ./ci-cd/VERSION).tgz ci-cd/package
helm show values ci-cd/helm > ci-cd/package/values.yaml

# deploy
# helm uninstall own-open-apis -n own
helm upgrade --install own-open-apis ci-cd/package/own-open-apis-$(cat ./ci-cd/VERSION).tgz --values ci-cd/package/values.yaml -n own --create-namespace

popd || exit