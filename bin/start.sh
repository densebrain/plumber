#!/bin/bash
JAVA_OPTS=""
if [ -e '/dev/urandom' ]; then
        echo 'Using urandom'
        JAVA_OPTS="-Djava.security.egd=file:/dev/urandom"
fi

JAR=`ls plumber*.jar`
PID=`ps aux | grep ${JAR} | grep -v grep | awk '{print $2}'`
if [ "$PID" != "" ]; then
        kill -9 $PID
fi


CMD="java $JAVA_OPTS -jar $JAR"
mkdir -p logs
$CMD  > logs/out