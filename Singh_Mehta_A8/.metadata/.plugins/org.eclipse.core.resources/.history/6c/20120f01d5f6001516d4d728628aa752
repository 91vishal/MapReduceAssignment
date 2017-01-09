
public class FileReader {
	
	public static FileData getFileData(String[] row){
		
		FileData record = new FileData();
		
		record.setWban(Integer.parseInt(row[FileConstants.INDEX_WBAN_NUMBER].trim()));
		record.setYearMonthDay(Integer.parseInt(row[FileConstants.INDEX_YEARMONTHDAY].trim()));
		record.setTime(Integer.parseInt(row[FileConstants.INDEX_TIME].trim()));
		record.setDryBulbTemp(Double.parseDouble(row[FileConstants.INDEX_DRY_BULB_TEMP].trim()));
		
		return record;
		
	}
	

}
 