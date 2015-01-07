#!/bin/bash

cd /tmp
rm -Rf plumber-tmp
mkdir plumber-tmp
cd plumber-tmp

if [ ! -f mvn-repo ]; then
	echo "Cloning repo"
	git clone -b mvn-repo --single-branch git@github.com:densebrain/plumber.git
fi

cd plumber
git checkout mvn-repo

git pull
if [ ! -f org ];then
	mkdir -p org
fi

cp -R ~/.m2/repository/org/plumber org/

git add -A
git commit -m 'Updating Repo'
git push
