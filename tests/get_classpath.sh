#!/bin/sh

# Set up the classpath variable to contain all the jar-files in the given
# jar-dirs.
jardirs=('/home/abbe/PUM/argouml/modules/argoprint/build' '/home/abbe/PUM/argouml/build');

for dir in ${jardirs[*]};
do
    for file in `ls $dir/*.jar`;
    do
        CLASSPATH=$CLASSPATH:$file;
    done;
done;

echo $CLASSPATH;
