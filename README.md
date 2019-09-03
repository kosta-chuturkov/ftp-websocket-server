Prerequisites before you run:
1. Install docker
2. Run docker run --name ftp-redis -p 127.0.0.1:6379:6379 -d redis
3. Run 
docker run --name postgres_ftp -e POSTGRES_PASSWORD=ribamech -e POSTGRES_DB=ftp_server -e POSTGRES_USER=ftp_user -p 127.0.0.1:5432:5432 -d postgres