import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


/*
 * ClusterAnalyis -- The map reduce program that emits the average
 * ticket prices for each airline in the year 2015. 
 * 
 */
object ClusterAnalysis {

	def main(args:Array[String]) {
		println("In Demo main");
		System.exit(ToolRunner.run(new ClusterAnalysis(), args));
	}

	/*
	 * Mapper program to read the entire data and filter out the flights which
	 * are not active in the year 2015 and passes the key value pair to the
	 * reduce program. Key is the carrier code and Value is the Avg. ticket
	 * price for that particular airline.
	 * 
	 */
	 class FlightMapper extends Mapper[Object, Text, Text, Text] {

		override def map(_K : Object, value : Text, context : Context) {
			
			var line = value.toString();
			var carrierCode = new String();
			var avgTicketPrice = Null;
			var year = 0;
			var month :String;
			var nullCheck = true;
			var replaceline = line.replace(", ", "?");
			
			var row = new Array[String]();	
			row = replaceline.split(",");

			try {
				year = row(0);
			} catch (NumberFormatException ex) {
				nullCheck = false;
			}

			if (row.length == 110 && year == 2015 && nullCheck) {
				carrierCode = row(6);
				avgTicketPrice = row(109);
				month = row(2);

				Text outKey = null;
				Text outValue = null;

				outKey = createKey(carrierCode, month);
				outValue = createValue(avgTicketPrice);

				context.write(outKey, outValue);
			}

		}

		def createKey(carrier : String, month : String) : Text = {
			
			Text returnKey = null;
			if (!carrier.isEmpty() && !month.isEmpty()) {
				returnKey = new Text(carrier.trim() + "\t" + month.trim());
			}
			return returnKey;
			}		
		

		def createValue(avgPrice : String) : Text = {
			Text returnValue = null;
			if (!avgPrice.isEmpty()) {
				returnValue = new Text(avgPrice);
			}
			return returnValue;
		}	
}

	/*
	 * Reducer program to emit the consolidated avg. ticket prices for each
	 * airline in the year 2015. It takes the key value pair from the mapper.
	 * key is the carrier code and Value is the avg. ticket prices for the
	 * airline.
	 * 
	 */
	class FlightReducer extends Reducer[Text, Text, Text, Text] {

		override def reduce(_K : Text, values: Iterable<Text>, context: Context)
				throws IOException, InterruptedException, IndexOutOfBoundsException {

			var sum = 0.0;
			var count = 0;
			for (Text value <- values) {
				sum = sum + Float.parseFloat(value.toString());
				count += 1;
			}

			float avg = (float) sum / count;
			context.write(_K, new Text(count + "\t" + String.valueOf(avg)));

		}
	}

	
	@Override
	override def run(args : Array[String]) {
		// TODO Auto-generated method stub

		if (args.length != 2) {
			error("Usage: ClusterAnalysis <input-path> <output-path>");
			return 1;
		}
		val job = Job.getInstance();

		job.setJarByClass(ClusterAnalysis.getClass);
		job.setMapperClass(classOf[FlightMapper]);
		job.setReducerClass(classOf[FlightReducer]);

		job.setMapOutputKeyClass(classOf[Text]);
		job.setMapOutputValueClass(classOf[Text]);
		job.setOutputKeyClass(classOf[Text]);
		job.setOutputValueClass(classOf[Text]);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		return job.waitForCompletion(true) ? 0 : 1;

	}
}
