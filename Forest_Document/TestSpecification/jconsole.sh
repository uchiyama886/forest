#!/bin/bash
# -*- coding: utf-8 -*-

# Usage: sh ./jconsole.sh <TARGET_DIR> <TARGET> <TARGET_JAR>
# sh ./jconsole.sh ./Scope_2 scope scope.jar 

TARGET_DIR=${1}
TARGET=${2}
TARGET_JAR=${3}
JPS=`/usr/libexec/java_home`/bin/jps
JCONSOLE=`/usr/libexec/java_home`/bin/jconsole

TICK_TIME=1

(cd ${TARGET_DIR} ; make test &)

TARGET_PID=''
while [ -z ${TARGET_PID} ]
do TARGET_PID=`${JPS} -lv 2> /dev/null | grep -E ${TARGET_JAR} | awk '{ print $1 }'`
done

${JCONSOLE} ${TARGET_PID}

kill -9 ${TARGET_PID}
sleep ${TICK_TIME}

exit 0
