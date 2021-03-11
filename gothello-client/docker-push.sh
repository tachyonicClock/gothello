#!/bin/sh
# tachyonic is my name on docker hub
img="tachyonic/gothello:client-$(git rev-parse --short HEAD)"
docker build -t $img .
docker push $img