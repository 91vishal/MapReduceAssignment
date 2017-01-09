

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;


public class S3Utils {

	static AmazonEC2 ec2 ;
	
	private static AWSCredentials getAWSCredentials() {
		
		AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (~/.aws/credentials), and is in valid format.",
                    e);
        }

		return credentials;
	}
	
	public static String retrieveInstanceId() throws IOException {
	    String EC2Id = null;
	    String inputLine;
	    URL EC2MetaData = new URL("http://169.254.169.254/latest/meta-data/instance-id");
	    URLConnection EC2MD = EC2MetaData.openConnection();
	    BufferedReader in = new BufferedReader(new InputStreamReader(EC2MD.getInputStream()));
	    while ((inputLine = in.readLine()) != null) {
	        EC2Id = inputLine;
	    }
	    in.close();
	    return EC2Id;
	}
	
	public static String fetchInstancePublicIP() throws IOException{
		
		ec2 = new AmazonEC2Client(getAWSCredentials());	
		ec2.setEndpoint("ec2.us-west-2.amazonaws.com");	
	    DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(retrieveInstanceId());
	    DescribeInstancesResult result= ec2.describeInstances(request);
	    List <Reservation> list  = result.getReservations();
	    String myIP = null;
	    
	    for (Reservation res:list) {
	         List <Instance> instanceList= res.getInstances();

	         for (Instance instance:instanceList){

	                 myIP = instance.getPublicIpAddress();
	         }     
	    }
	    
	    return myIP;
	}
}
