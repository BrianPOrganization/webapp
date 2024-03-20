#! /bin/bash
echo "########## Starting the Run configuration ##########"
echo "########## Creating a no-login-user ##########"
sudo groupadd csye6225
sudo useradd -s /usr/sbin/nologin -g csye6225 -m csye6225
sudo mkdir /opt/application
sudo mv /tmp/application.service /etc/systemd/system/application.service
echo "########## Logging configuration ##########"
sudo mkdir /var/log/csye6225
sudo touch /var/log/csye6225/application.log
sudo chown -R csye6225:csye6225 /var/log/csye6225/application.log
echo "########## Moving necessary files ##########"
sudo mv /tmp/application-0.0.1-SNAPSHOT.jar /opt/application/application-0.0.1-SNAPSHOT.jar
sudo chown -R csye6225:csye6225 /opt/application/application-0.0.1-SNAPSHOT.jar
echo "########## Creating a service ##########"
sudo systemctl daemon-reload
sudo systemctl enable application
echo "########## Adding ops agent ##########"
curl -sSO https://dl.google.com/cloudagents/add-google-cloud-ops-agent-repo.sh
sudo bash add-google-cloud-ops-agent-repo.sh --also-install
echo "########## moving the config.yml ##########"
if [ -f /tmp/config.yaml ]; then
    echo "File exists"
    ls -l /etc/google-cloud-ops-agent
fi
sudo cp -f /tmp/config.yaml /etc/google-cloud-ops-agent/config.yaml
sudo systemctl restart google-cloud-ops-agent
sudo systemctl status google-cloud-ops-agent.service
sudo journalctl -xe