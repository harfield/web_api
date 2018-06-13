#!/bin/bash

work_dir=$(cd $(dirname $0); pwd)

nohup java -cp ${work_dir}/../etc:${work_dir}/../jars/web_api-2.0.1.RELEASE-jar-with-dependencies.jar com.fancydsp.data.ApplicationServer  2>&1 &