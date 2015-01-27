#!/bin/bash

BASEDIR=$(pwd)
TMPBASE=/tmp/plumber-tmp/plumber
SERVERPKG=/tmp/plumber-$1.zip

echo "Building Archive"

rm -Rf ${TMPBASE}
mkdir -p ${TMPBASE}
cp manager/target/libs/manager-$1.jar ${TMPBASE}/plumber-latest.jar

cp bin/* ${TMPBASE}
cd ${TMPBASE}/../
zip -r $SERVERPKG plumber
cd $BASEDIR

echo "Releasing"

github-release delete \
	-s $GITTOKEN \
    --user densebrain \
    --repo plumber \
    --tag v$1

github-release release \
	-s $GITTOKEN \
    --user densebrain \
    --repo plumber \
    --tag v$1 \
    --name "plumber-v$1" \
    -d "plumber-v$1"

github-release upload \
	-s $GITTOKEN \
	-u densebrain \
	--repo plumber \
	--tag v$1 \
	--name "plumber-v$1.zip" \
	--file $SERVERPKG