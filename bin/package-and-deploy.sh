#!/bin/bash

mvn clean package
cp target/flink-pulsar-playground-1.0-SNAPSHOT.jar ~/programming/tme/projects/misc/docker/pulsar/functions