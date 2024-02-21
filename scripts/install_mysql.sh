#! /bin/bash
echo "########## Installing MySQL Server ##########"
sudo yum install mysql-server -y
sudo systemctl start mysqld
mysqladmin -u root password password
sudo systemctl enable mysqld
echo "########## MySQL Server installed ##########"