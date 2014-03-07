#!/bin/bash

_jar=`ls lib | grep "..*\.jar$"`
classpath='. '${_jar}
classpath=`echo ${classpath} | sed -e 's/ /:lib\//g'`
java -cp ${classpath} com.chenjw.imagegrab.StartMain $*
