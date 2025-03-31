#!/bin/bash

# Update and upgrade system packages
sudo apt update -y && sudo apt upgrade -y

# Install unzip
sudo apt install unzip -y

echo "Unzip installed successfully."

# Install AWS CLI
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

# Verify AWS CLI installation
aws --version
echo "AWS CLI installed successfully."

# Install Java (OpenJDK 21)
sudo apt install openjdk-21-jdk -y

# Verify Java installation
java -version
echo "Java installed successfully."

# Install Docker
sudo apt install docker.io -y

# Start and enable Docker service
sudo systemctl start docker
sudo systemctl enable docker

# Add current user to Docker group (to run without sudo)
sudo usermod -aG docker $USER
sudo usermod -aG docker jenkins

# Verify Docker installation
docker --version
echo "Docker installed successfully."


#Jenkins
wget -q -O - https://pkg.jenkins.io/debian/jenkins.io.key | sudo apt-key add - 

sudo sh -c 'echo deb http://pkg.jenkins.io/debian-stable binary/ > /etc/apt/sources.list.d/jenkins.list'

sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 5BA31D57EF5975CA

sudo apt update && sudo apt install jenkins

sudo systemctl start jenkins

sudo systemctl enable jenkins

sudo cat /var/lib/jenkins/secrets/initialAdminPassword

echo "All required tools have been installed successfully."
