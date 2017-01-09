#Author: Vishal Mehta, Harshali Singh

from subprocess import call
import sys
import boto3
import math
import paramiko

## Open the file with read only permit
f = open('dns.txt', "r")

## use readlines to read all lines in the file
line = f.readline()
dnslist = list()

while line:
	#print (line)
	dnslist.append(line.strip())	
	line = f.readline()
f.close()

numProc = len(dnslist)
s3 = boto3.resource('s3')
files = list()

#retrieve all files from S3 bucket and add those to the list
for bucket in s3.buckets.all():
	for obj in bucket.objects.filter(Prefix='input/'):
		filename = '{0}'.format(obj.key).split("/")[1]
		if filename != "":
			files.append(filename)


#Divide the entire list of files, seq into chunks of equal/unequal sizes based on number of instances
def chunkIt(seq, num):
  avg = len(seq) / float(num)
  last = 0.0
  out = []
  while last < len(seq):
    out.append(seq[int(last):int(last + avg)])
    last += avg

  return out

#print (len(chunkIt(files, numProc)))
ssh = paramiko.SSHClient()
def connectssh(dns):
	print (dns)
	ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
	ssh.connect(dns,username='ec2-user',key_filename='EC2_KP.pem')

def putec2(part):
	partlength = len(part)
	for i in range(partlength):
		print ("Transferring chunks of data...")
		cmd="aws s3 cp s3://cs6240-viha/input/" + part[i] + " ~/input"	
		stdin, stdout, stderr = ssh.exec_command(cmd)

chunks = chunkIt(files, numProc)

#print (len(dnslist))
i=0
for dns in dnslist:
	#print (dns)
	connectssh(dns)	
	putec2(chunks[i])
	i=i+1

