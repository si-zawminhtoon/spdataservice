#!/bin/bash

original_owner=$(stat --format=%u /var/lib/mysql)
original_group=$(stat --format=%g /var/lib/mysql)

source /root/settings.conf

if [ ! -e /var/lib/mysql/ibdata1 ] ; then
  mysqld \
    --no-defaults \
    --initialize-insecure \
    --basedir=/usr \
    --datadir=/var/lib/mysql \
    --user=mysql
  chown -R mysql: /var/lib/mysql
  /usr/sbin/mysqld --user=mysql --daemonize
  # mysql -e "CREATE USER 'root'@'%'; GRANT ALL ON *.* TO root@'%' WITH GRANT OPTION; "
  mysql -e "CREATE USER 'root'@'%' IDENTIFIED BY '$DB_ROOT_PW'; GRANT ALL PRIVILEGES ON *.* TO 'root'@'%'; "
  mysql_tzinfo_to_sql /usr/share/zoneinfo | mysql mysql
  mysql -e "CREATE DATABASE appdb; "
  mysql -e "CREATE USER appuser IDENTIFIED BY '$USER_PW'; "
  mysql -e "GRANT ALL PRIVILEGES ON appdb.* TO 'appuser'@'%'; "
  mysql -e "FLUSH PRIVILEGES; "
  mysql < /root/init.sql
  # mysql -e "ALTER USER 'root'@'%' IDENTIFIED by '$DB_ROOT_PW'; "
  mysqladmin shutdown
fi

chown -R mysql: /var/lib/mysql
/usr/sbin/mysqld --user=mysql "$@"

chown -R $original_owner:$original_group /var/lib/mysql