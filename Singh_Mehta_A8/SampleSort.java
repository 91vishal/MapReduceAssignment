import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.amazonaws.services.s3.model.S3Object;

public class SampleSort {

	private static final String folder = "<folder_name>";
	private static final int NUM_NODES = 8;
	//private static final String key = "*.txt.gz";
	
	private static int processors;
	private CyclicBarrier barrier;
	private ExecutorService threads;
	List<SorterHelper> sorters;

	public SampleSort() {	
		this(Runtime.getRuntime().availableProcessors());
	}

	public SampleSort(int numNodes) {
		processors = numNodes;
		barrier = new CyclicBarrier(processors + 1);
		threads = Executors.newFixedThreadPool(processors);
	}
	
	/**
	 * Phase 1 - Each process sorts its local share of the initial elements. 
	 * 
	 * @throws BrokenBarrierException
	 * @throws InterruptedException
	 */
	private static List<FileData> sortLocalData(List<FileData> localData) throws InterruptedException{
		
		Collections.sort(localData);		
		
		return localData;
	}
	
	/**
	 * Phase 1.5 - Each process gets a regular sample of its locally sorted
	 * block.
	 * 
	 * @param sorters
	 * @return
	 */
	private static List<FileData> sampleSections(List<FileData> sortedLocalData) {
		
		List<FileData> samples = new ArrayList<FileData>();
		SorterHelper sh = new SorterHelper(sortedLocalData);
		samples.addAll(sh.getSamples(processors));
		
		return samples;
	}
	
	/**
	 * Phase 2 - One process gathers and sorts the local regular samples. It
	 * selects p - 1 pivot values from the sorted list of regular samples. Each
	 * process partitions its sorted sublist into p disjoint pieces, using the
	 * pivot values as separators between the pieces.
	 * 
	 * @throws InterruptedException
	 */
	private static List<FileData> getPivotsFromSamples(List<FileData> samples)
			throws InterruptedException {
		
		SorterHelper sh = new SorterHelper(samples);
		List<FileData> pivots = sh.getSamples(processors);
		return pivots.subList(pivots.size() - (processors - 1), pivots.size());
	}
	
	/**
	 * Phase 3 - Each process i keeps its ith partition and sends the jth
	 * partition to process j, for all j != i.
	 * 
	 * @throws BrokenBarrierException
	 * @throws InterruptedException
	 */
	private void distributePartitions(List<FileData> points) throws InterruptedException {

		List<List<List<FileData>>> sectionList = new ArrayList<List<List<FileData>>>();
		
		SorterHelper sh = new SorterHelper();
		sectionList.add(sh.getSections(points));

		// merge the section sections at the same indices.
		sorters.clear();

		for (int i = 0; i < processors; i++) {
			List<T> l = new ArrayList<T>();

			for (int j = 0; j < processors; j++) {
				if (sectionList.get(j).size() > i) { // This check here may not
														// be needed - above is
														// the issue
					l.addAll(sectionList.get(j).get(i)); // IndexOutOfBoundsException
				}
			}
			PSRSQuickSorterTask<T> s = new PSRSQuickSorterTask<T>(l, barrier);
			// qs called
			threads.execute(s);

			sorters.add(s);
		}

		barrier.await();
		barrier.reset();
	}


	public static void main(String[] args) throws IOException{

		if(args.length != 3){

			System.out.println("Usage: SampleSort Dry Bulb Temps3://cs6240sp16/climate s3://your-bucket/output");
		}

		List<FileData> unsortedData = S3Utils.readFromS3(folder);
		SampleSort ss = new SampleSort(NUM_NODES);
		
		try {
			
			// PHASE ONE
			List<FileData> sortedLocalData = sortLocalData(unsortedData);
			
			// PHASE ONE POINT FIVE
			List<FileData> samples = sampleSections(sortedLocalData);

			// PHASE TWO
			List<FileData> points = getPivotsFromSamples(samples);

			// PHASE THREE
			distributePartitions(sorters, points);

			// PHASE FOUR
			List<T> sorted = mergePartitions(sorters);

			threads.shutdown();

			return sorted;
			
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}	

	}

}