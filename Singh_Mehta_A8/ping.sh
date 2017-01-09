#Author: Vishal Mehta	
#!/bin/bash
# Ping instances

while true; do
	status=$(aws ec2 describe-instances --filters "Name=instance-type,Values=t2.medium" | jq -r ".Reservations[].Instances[0].State.Name")
	echo "$status"
	if [ $status == "running" ]; then
		echo "Running ####"
		exit	
	else 
		sleep 5
		echo "Waiting for instance to start"	
	fi
done

