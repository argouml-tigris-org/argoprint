#!/bin/bash

# Set up the classpath variable to contain all the jar-files in the given
# jar-dirs.
jardirs=('../build' '../../../build' '../../../tools/junit-3.8.1');

for dir in ${jardirs[*]};
do
    for file in `ls $dir/*.jar`;
    do
        CLASSPATH=$CLASSPATH:$file;
    done;
done;

export CLASSPATH;

# Start the junit test runner.
java junit.swingui.TestRunner &
