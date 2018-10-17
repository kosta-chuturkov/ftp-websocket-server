#!/bin/sh
set -x

export FTP_DB_HOST=localhost
export FTP_DB_NAME=ftp_server
export FTP_DB_USER=ftp_user
export DB_ADMIN_USER=postgres
export FTP_DB_PASSWORD=ribamech

CREATE_USER_SQL=$(cat ./create-user.sql | sed -e "s/FTP_DB_USER/$FTP_DB_USER/g" | sed -e "s/FTP_DB_PASSWORD/$FTP_DB_PASSWORD/g")

export PGPASSWORD=postgres && \
psql postgres $DB_ADMIN_USER -h $FTP_DB_HOST -c "$CREATE_USER_SQL" && \
export PGPASSWORD=$FTP_DB_PASSWORD && \
createdb -h $FTP_DB_HOST -U $FTP_DB_USER $FTP_DB_NAME