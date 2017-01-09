#Author: Vishal Mehta
#!/bin/bash
# Spawn N instances

cmds="sudo yum install java-1.7.0-openjdk java-1.7.0-openjdk-devel -y; export JAVA_HOME=/usr/lib/jvm/jre-1.7.0-openjdk.x86_64"

if [ -z "$1" ]; then 
	echo "Usage: ./start.sh N<number of nodes>"
else 
#create instances
aws ec2 run-instances --image-id ami-c229c0a2 --iam-instance-profile Name="s3access" --count $1 --instance-type t2.medium --key-name EC2_KP --security-groups my-sg
sleep 25

aws ec2 describe-instances --filters "Name=instance-type,Values=t2.medium" | jq -r ".Reservations[].Instances[].NetworkInterfaces[0].Association.PublicIp" > nodelist.txt

#fetch publicDNS
aws ec2 describe-instances --filters "Name=instance-type,Values=t2.medium" | jq -r ".Reservations[].Instances[].PublicDnsName" > dns.txt
publicdns=$(aws ec2 describe-instances --filters "Name=instance-type,Values=t2.medium" | jq -r ".Reservations[].Instances[].PublicDnsName")
publicdnsarr=( $publicdns )

#echo "${#publicdnsarr[@]}"
dns_len=${#publicdnsarr[@]}

#ssh to each instance
for (( i=0; i<${dns_len}; i++ ));
do
	echo ${publicdnsarr[i]}
	ssh -i EC2_KP.pem ec2-user@${publicdnsarr[i]} "${cmds}"
	scp -i EC2_KP.pem nodelist.txt ec2-user@${publicdnsarr[i]}:~/input
	#scp -i EC2_KP.pem node.jar ec2-user@${publicdnsarr[i]}:~/input2
done
fi


