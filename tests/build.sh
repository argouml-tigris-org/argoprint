#!/bin/bash

# Set up the classpath variable to contain all the jar-files in the given
# jar-dirs.
JARDIRS=('../build' '../../../build' '../../../tools/junit-3.8.1');

for DIR in ${JARDIRS[*]}; do
    for FILE in `ls $DIR/*.jar`; do
        CLASSPATH=$CLASSPATH:$FILE;
    done;
done;

export CLASSPATH;

JAVAFILES=(`find . -name *.java`);

for FILE in ${JAVAFILES[*]}; do

    RESULT=`javac $FILE 2>&1 | sed -e 's/\(^\.\/.\+\.java\):\([0-9]\+\):/[33:40m\1[0m:[32:40m\2[0m:/'`
    if [ "$RESULT" ]; then
        echo "$RESULT";
    fi;

done;
