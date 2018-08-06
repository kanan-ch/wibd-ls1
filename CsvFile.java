package wibd.ml.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class CsvFile {

	private File file = null;
	StringBuilder builder = new StringBuilder();
	String ColumnNamesList = "text, File Name,Patended Date, Patend Number, Time Taken";
	PrintWriter pw = null;
	public CsvFile(String filePath ) {
		try {
			file = new File(filePath);
			pw = new PrintWriter(file);
			builder.append(ColumnNamesList + "\n");
			pw.write(builder.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(
					new File("D:\\projects\\machine_learning\\Women_in_BIg-Data\\results\\extractedText.csv"));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		StringBuilder builder = new StringBuilder();
		String ColumnNamesList = "File Name,Patended Date, Patend Number, Time Taken";
// No need give the headers Like: id, Name on builder.append
		builder.append(ColumnNamesList + "\n");
		builder.append("1" + ",");
		builder.append("Chola");
		builder.append('\n');
		pw.write(builder.toString());
		pw.close();
		System.out.println("done!");
	}

	public void writeToCSV(String text, String fileName, String date, String patentNumber, long timeTaken) {
		if(pw == null) {
			try {
				pw = new PrintWriter(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		builder.delete(0, builder.length());
		builder.append(text.replace(",",  ":") + ",");
		builder.append(fileName + ",");
		builder.append(date + ",");
		builder.append(patentNumber + ",");
		builder.append(timeTaken);
		builder.append('\n');
		pw.write(builder.toString());
	}
	public void close() {
		if(pw != null) {
			pw.close();
		}
	}
}
