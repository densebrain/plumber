#!/bin/bash

service plumber stop

rm /etc/init/plumber.conf
ln -s /opt/plumber/init.conf /etc/init/plumber.conf
initctl reload-configuration

cd /opt/plumber
mkdir -p logs
mkdir -p extras
service plumber start