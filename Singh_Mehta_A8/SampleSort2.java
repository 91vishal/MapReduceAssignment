import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.io.InputStreamReader;


//import com.amazonaws.services.s3.model.S3Object;

public class SampleSort implements Serializable{

	private static final String folder = "<folder_name>";
	private static final int NUM_NODES = 8;
	//private static final String key = "*.txt.gz";

	public static void main(String[] args) throws IOException{

		if(args.length != 3){

			System.out.println("Usage: SampleSort Dry Bulb Temps3://cs6240sp16/climate s3://your-bucket/output");
		}
		buildData();
	}
	
	public static List<FileData> buildData() throws IOException{

	

		//List<FileData> temp = S3Utils.readFromS3(folder);
		List<FileData> temp = new ArrayList<FileData>();
		
		
		
		
		
		BufferedReader br = null;
		try {
			FileInputStream inputStream = new FileInputStream("/Users/Akanksha/Downloads/199607hourly.txt");
			br = new BufferedReader(new InputStreamReader(inputStream));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//BufferedReader br = new BufferedReader(new FileReader("G:\\MR\\199607hourly.txt"));
		//BufferedReader b = new BufferedReader("); 
		String data;
		
	
         String line = null;
        // System.out.println(br.readLine());
        // to remove the header
         //br.readLine();
        // System.out.println(br.readLine());
         try {
 			while((line = br.readLine()) != null) {
 				
 				String[] row = line.split(",");
 				//checkSanity(row);
 				//System.out.println("wban " + row[0] + " yearmonthday " + row[1] + " time "+row[2] + " dry "+row[8]);
 				FileData data21 = FileReader.getFileData(row);
 				
 				temp.add(data21);
 				
 			
 			}  
 		} catch (IOException e) {

 			e.printStackTrace();
 		}
		
		Collections.sort(temp);
		
		
		
		SorterHelper sortHelper = new SorterHelper(temp);
		//System.out.println(sortHelper);
		List<FileData> samples = sortHelper.getSamples(NUM_NODES);
		
		return samples;
		
		
		
		
		
//		SorterHelper sortHelper2 = new SorterHelper(samples);
		// Adding threads for reading all the samples across processors.
		
		/*Thread runner = new Thread(sortHelper2);
		runner.start();
		runner.join();*/
		 
		//List<FileData> pivots = sortHelper2.getSamples(NUM_NODES);
		
		
	//System.out.println(pivots.subList(pivots.size() - (NUM_NODES - 1), pivots.size()));
		
		
		

	}

}