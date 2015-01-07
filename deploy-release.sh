#!/bin/bash

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
    --name "manager-v$1" \
    -d "manager-v$1"

github-release upload \
	-s $GITTOKEN \
	-u densebrain \
	--repo plumber \
	--tag v$1 \
	--name "manager-v$1" \
	--file manager/target/libs/manager-$1.jar