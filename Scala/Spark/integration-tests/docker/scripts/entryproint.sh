#!/bin/bash

set -euo pipefail

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/

/etc/init.d/postgresql start

FILE='/opt/first_time'
if [ ! -f "$FILE" ]; then
    su -c 'psql -f create_user.sql' - postgres
    /usr/hdp/3.1.4.0-315/hive/bin/schematool -dbType postgres -initSchema
    touch /opt/first_time
fi

hive --service metastore
