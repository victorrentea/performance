cd C:\workspace\redis-5.0.7-docker
docker stop redis-local
docker rm redis-local
docker run -d -p 6379:6379 --name redis-local -d redis
