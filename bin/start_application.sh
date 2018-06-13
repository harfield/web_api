#!/bin/bash

work_dir=$(cd $(dirname $0); pwd)
export JAVA_HOME=/usr/java/jdk1.8.0_101/

nohup $JAVA_HOME/bin/java -cp ${work_dir}/../etc:${work_dir}/../jars/web_api-2.0.1.RELEASE.jar com.fancydsp.data.ApplicationServer  > ${work_dir}/../logs/trace.log 2>&1 &