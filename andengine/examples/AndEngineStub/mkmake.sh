#!/usr/bin/env bash


#cp Makefile.build AndEngineExamples/Makefile

echo link: > Makefile
ls -l | grep ^d| awk '{print "\tln -s ../local.properties " $9}' >> Makefile
echo        >> Makefile
echo clean: >> Makefile
ls -l | grep ^d| awk '{print "\trm "$9"/local.properties"}' >> Makefile

make

exit 0
