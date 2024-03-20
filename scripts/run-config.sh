#! /bin/bash
echo "########## Starting the Run configuration ##########"
echo "########## Creating a no-login-user ##########"
sudo groupadd csye6225
sudo useradd -s /usr/sbin/nologin -g csye6225 -m csye6225
sudo mkdir /opt/application
sudo mv /tmp/application.service /etc/systemd/system/application.service
echo "########## Logging configuration ##########"
sudo touch /var/log/csye6225.log
sudo chown -R csye6225:csye6225 /var/log/csye6225.log
echo "########## Moving necessary files ##########"
sudo mv /tmp/application-0.0.1-SNAPSHOT.jar /opt/application/application-0.0.1-SNAPSHOT.jar
sudo chown -R csye6225:csye6225 /opt/application/application-0.0.1-SNAPSHOT.jar
echo "########## Creating a service ##########"
sudo systemctl daemon-reload
sudo systemctl enable application
echo "########## Adding ops agent ##########"
curl -sSO https://dl.google.com/cloudagents/add-google-cloud-ops-agent-repo.sh
sudo bash add-google-cloud-ops-agent-repo.sh --also-install