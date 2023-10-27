FROM mysql:8.0.28

COPY ./conf/my.cnf /etc/my.cnf

COPY entrypoint.sh /root/entrypoint.sh

COPY ./conf/settings.conf /root/settings.conf

COPY init.sql /root/init.sql

RUN chmod 777 /root/entrypoint.sh

EXPOSE 3306

ENTRYPOINT ["/root/entrypoint.sh"]