#!/bin/sh

# Set up the classpath variable to contain all the jar-files in the given
# jar-dirs.
jardirs=('/home/abbe/PUM/argouml/modules/argoprint/build' '/home/abbe/PUM/argouml/build');

for dir in ${jardirs[*]}; do
    for file in `ls $dir/*.jar`; do
        CLASSPATH=$CLASSPATH:$file;
    done;
done;

export CLASSPATH;

javafiles=`find . -name *.java`;

for file in ${javafiles[*]}; do

    result=`javac $file 2>&1 | sed -e 's/\(^\.\/.\+\.java\):\([0-9]\+\):/[33:40m\1[0m:[32:40m\2[0m:/'`
    if [ "$result" ]; then
        echo "$result";
    fi;

done;
