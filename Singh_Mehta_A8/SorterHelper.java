import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class SorterHelper{
	
	private List<FileData> sortedList;
	
	public SorterHelper(List<FileData> sorted){
		
		this.sortedList = sorted;
	}

	public List<FileData> getSamples(int processors) {
		
		if (sortedList == null) {
			
			System.out.println("Not sorted");
		}

		List<FileData> samples = new ArrayList<FileData>();

		for (int i = 0; i < processors; i++) {
			samples.add(sortedList.get(i * sortedList.size() / (processors)));
		}

		return samples;
	}
	
	
	public List<List<FileData>> getSections(List<FileData> points) {
		
		if (sortedList == null) {
			
			System.out.println("Not sorted");
		}

		List<List<FileData>> sections = new ArrayList<List<FileData>>();

		int currentPointIndex = 0;
		int from = 0;
		FileData point = points.get(currentPointIndex);

		for (int i = 0; i < sortedList.size(); i++) {
			if (sortedList.get(i).compareTo(point) == 1) {
				sections.add(new ArrayList<FileData>(sortedList.subList(from, i)));
				from = i;
				currentPointIndex += 1;

				if (currentPointIndex >= points.size()) {
					break;
				}

				point = points.get(currentPointIndex);
			}
		}

		sections.add(new ArrayList<FileData>(sortedList.subList(from,
				sortedList.size())));
		return sections;
	}
	
	public List<FileData> getSortedList() {
		return sortedList;
	}
	
	public int getSize() {
		return sortedList.size();
	}
		
	
}
