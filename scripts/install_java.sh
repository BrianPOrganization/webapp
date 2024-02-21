#! /bin/bash
echo "########## Installing Java 17 script ##########"
sudo dnf install java-17-openjdk-devel -y
echo java -version
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
if [ -z "$JAVA_HOME" ]; then
    echo "JAVA_HOME not found"
    exit 1
else
  echo "########## Java 17 installed ##########"
fi