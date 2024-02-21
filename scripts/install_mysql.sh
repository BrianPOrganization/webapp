#! /bin/bash
echo "########## Installing MySQL Server ##########"
sudo yum install mysql-server -y
sudo systemctl start mysqld
mysqladmin -u root password password
sudo systemctl enable mysqld
if [ $? -ne 0 ]; then
    echo "MySQL Server not installed"
    exit 1
fi
echo "########## MySQL Server installed ##########"