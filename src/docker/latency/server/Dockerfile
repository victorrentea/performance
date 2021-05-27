FROM ubuntu:18.04
WORKDIR /work
RUN apt-get update && \
    apt-get install software-properties-common -y && \
    add-apt-repository ppa:longsleep/golang-backports && \
    apt-get update && \
    apt-get install golang-go iproute2 -y
ADD main.go .
CMD ["go", "run", "main.go"]