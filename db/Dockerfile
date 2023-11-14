# FROM mysql:8.0.28
FROM hoshinotsuyoshi/mysql:5.7.12

COPY ./conf/my.cnf /etc/my.cnf

COPY entrypoint.sh /root/entrypoint.sh

COPY ./conf/settings.conf /root/settings.conf

COPY init.sql /root/init.sql

RUN chmod 777 /root/entrypoint.sh

RUN chmod 777 /var/log

EXPOSE 3306

ENTRYPOINT ["/root/entrypoint.sh"]