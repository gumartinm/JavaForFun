FROM debian:stretch

# Repositories installation
RUN apt-get update
RUN apt-get install -y gnupg
ADD repositories/hdp.list /etc/apt/sources.list.d/
RUN apt-key adv --recv-keys --keyserver keyserver.ubuntu.com B9733A7A07513CAD
RUN apt-get update

# Hive
RUN apt-get install -y openjdk-8-jdk
RUN apt-get install -y postgresql
RUN apt-get install -y hive
ADD configurations/hive-site.xml /etc/hive/conf/hive-site.xml
ADD configurations/hive-env.sh /etc/hive/conf/hive-env.sh

# PostgreSQL
RUN echo 'host all hive  127.0.0.1/32   trust' >> /etc/postgresql/9.6/main/pg_hba.conf

ADD scripts/create_user.sql /var/lib/postgresql/
ADD scripts/entryproint.sh /

ENTRYPOINT ["/entryproint.sh"]
