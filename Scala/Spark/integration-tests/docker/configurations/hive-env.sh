
# The heap size of the jvm, and jvm args stared by hive shell script can be controlled via:
if [ "$SERVICE" = "metastore" ]; then

  export HADOOP_HEAPSIZE=4096 # Setting for HiveMetastore
  export HADOOP_OPTS="$HADOOP_OPTS -Xloggc:/var/log/hive/hivemetastore-gc-%t.log -XX:+UseG1GC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCCause -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=10M -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/log/hive/hms_heapdump.hprof -Dhive.log.dir=/var/log/hive -Dhive.log.file=hivemetastore.log"

fi

if [ "$SERVICE" = "hiveserver2" ]; then

  export HADOOP_HEAPSIZE=4096 # Setting for HiveServer2 and Client
  export HADOOP_OPTS="$HADOOP_OPTS -Xloggc:/var/log/hive/hiveserver2-gc-%t.log -XX:+UseG1GC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCCause -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=10M -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/log/hive/hs2_heapdump.hprof -Dhive.log.dir=/var/log/hive -Dhive.log.file=hiveserver2.log"

fi



export HADOOP_CLIENT_OPTS="$HADOOP_CLIENT_OPTS  -Xmx${HADOOP_HEAPSIZE}m"
export HADOOP_CLIENT_OPTS="$HADOOP_CLIENT_OPTS"

# Larger heap size may be required when running queries over large number of files or partitions.
# By default hive shell scripts use a heap size of 256 (MB).  Larger heap size would also be
# appropriate for hive server (hwi etc).


# Set HADOOP_HOME to point to a specific hadoop install directory
HADOOP_HOME=${HADOOP_HOME:-/usr/hdp/current/hadoop-client}

export HIVE_HOME=${HIVE_HOME:-/usr/hdp/current/hive-server2}

# Hive Configuration Directory can be controlled by:
export HIVE_CONF_DIR=${HIVE_CONF_DIR:-/usr/hdp/current/hive-server2/conf/}

# Folder containing extra libraries required for hive compilation/execution can be controlled by:
if [ "${HIVE_AUX_JARS_PATH}" != "" ]; then
  if [ -f "${HIVE_AUX_JARS_PATH}" ]; then
    export HIVE_AUX_JARS_PATH=${HIVE_AUX_JARS_PATH}
  elif [ -d "/usr/hdp/current/hive-webhcat/share/hcatalog" ]; then
    export HIVE_AUX_JARS_PATH=/usr/hdp/current/hive-webhcat/share/hcatalog/hive-hcatalog-core.jar
  fi
elif [ -d "/usr/hdp/current/hive-webhcat/share/hcatalog" ]; then
  export HIVE_AUX_JARS_PATH=/usr/hdp/current/hive-webhcat/share/hcatalog/hive-hcatalog-core.jar
fi

export METASTORE_PORT=9083

