docker stop redis-local
docker rm redis-local
docker run -d -p 6379:6379 --name redis-local -d redis
