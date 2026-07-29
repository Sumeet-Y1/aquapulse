FROM ubuntu:latest
LABEL authors="echof"

ENTRYPOINT ["top", "-b"]