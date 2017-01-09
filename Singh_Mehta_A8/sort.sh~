#Author: Vishal Mehta
#!/bin/bash

#ssh to each instance
while read line; do
	#echo "$line"
	ssh -i EC2_KP.pem ec2-user@${line} "cd input2; javac HelloWorld.java; java HelloWorld" & < /dev/null
	#credentials for s3 bucket
	#java -jar node.jar &
done < dns.txt




