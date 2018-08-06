package wibd.ml.adapter;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.Charset;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import wibd.ml.util.CsvFile;

public class TextExtracter {
	private CsvFile csv = new CsvFile(
			"D:\\projects\\machine_learning\\Women_in_BIg-Data\\results\\extractedText_year_n_patentNo_1.csv");

	public String getImgText(String imageLocation, String fileName) {
		System.out.println("image loc: " + imageLocation);
		ITesseract instance = new Tesseract();
		try {
			long startTime = System.currentTimeMillis();
			String imgText = instance.doOCR(new File(imageLocation), new Rectangle(0, 0, 1974, 500));
			String[] imgtextSplit = imgText.split("[\n]");
			if (imgtextSplit != null && imgtextSplit.length > 0) {
				parseText(imgtextSplit[0], fileName, startTime);
			}
			return imgText;
		} catch (TesseractException e) {
			e.getMessage();
			return "Error while reading image";
		}
	}

	public String getImgText(BufferedImage img, String fileName) {
		System.out.println("image type: " + img.getType());
		ITesseract instance = new Tesseract();
		try {
			long startTime = System.currentTimeMillis();
			String imgText = instance.doOCR(img, new Rectangle(0, 0, 1974, 500));

			String[] imgtextSplit = imgText.split("[\n]");
			if (imgtextSplit != null && imgtextSplit.length > 0) {
//				parseText(imgtextSplit[0],fileName, startTime);
				parseYearAndPatentNumber(imgtextSplit[0], fileName, startTime);
			}
			return imgText;
		} catch (TesseractException e) {
			e.getMessage();
			return "Error while reading image";
		}
	}

	/**
	 * 
	 * @return year and patent number in the format "YEAR-PATENT_Number"
	 */
	private void parseText(String text, String fileName, long startTime) {
		System.out.println("text to parse is " + text);
		String[] strs = text.split("[.]");
		String year = null;
		String month = null;
		String date = null;
		String patentNumber = null;
		for (int i = 0; i < strs.length; i++) {
			String str = strs[i].trim();
			System.out.println("original text is " + str);
			if (year == null && str.length() > 4) {
				String[] s = str.split(" ");
				for (String temp : s) {
					year = getYear(temp);
					if (year != null) {
						break;
					}
				}
			} else if (year == null && str.length() == 4) {
				year = getYear(str);
			} else if (year != null && month == null) {
				month = getMonth(str);
				if (checkSplitLength(str) > 1) {
					date = getDate(str);
				}
			} else if (year != null && month != null && date == null) {
				date = getDate(str);
			} else if (year != null && month != null && date != null && patentNumber == null) {
				patentNumber = getPatentNumber(str);
			}
		}
		System.out.println("DAte of PAtented : " + year + " " + month + " " + date);
		System.out.println("Patent Number : " + patentNumber);
		long endTime = System.currentTimeMillis();
		System.out.println("Time taken to extract the text : " + (endTime - startTime));
		csv.writeToCSV(text, fileName, year + " " + month + " " + date, patentNumber, (endTime - startTime));
	}

	public void close() {
		csv.close();
	}

	private String getYear(String text) {
		try {
			if (text != null && text.length() == 4 && Integer.valueOf(text) != null) {
				return text;
			}
		} catch (NumberFormatException e) {
		}
		return null;
	}

	private String getMonth(String text) {
		if (text != null && text.length() > 0) {
			text = text.trim();
			String[] s = text.split(" ");
			return s[0];
		}
		return null;
	}

	private String getDate(String text) {
		if (text != null && text.length() > 0) {
			text = text.trim();
			String[] s = text.split(" ");
			if (s.length > 1) {
				return s[1];
			}
			return s[0];
		}
		return null;
	}

	private String getPatentNumber(String text) {
		if (text == null) {
			return null;
		}
		String[] strs = text.split(" ");
		for (String str : strs) {
			str = str.replace(",", "");
			try {
				if (Integer.valueOf(str) != null) {
					return str;
				}
			} catch (NumberFormatException e) {

			}
		}
		return null;
	}

	private int checkSplitLength(String text) {
		if (text != null) {
			text = text.trim();
			return text.split(" ").length;
		}
		return 1;
	}

	private void parseYearAndPatentNumber(String text, String fileName, long startTime) {
		System.out.println("text is " + text);
		Charset charset = Charset.forName("UTF-8");
		text = charset.decode(charset.encode(text)).toString();
		System.out.println("after decoding : " + text);
		text = text.replaceAll("[^A-Z0-9 ]", "");
		String[] words = text.trim().split(" ");
		String year = null;
		String patentNumber = null;
		for (String word : words) {
			// removes all alphanumeric character except space
			System.out.println("word is " + word);
			if (year == null) {
				if (word.length() == 4)
					try {
						Integer.valueOf(word);
						year = word;
					} catch (NumberFormatException e) {
						System.out.println("Error in parsing year : " + e.getMessage());
					}
			} else {
				try {
					Integer.valueOf(word);
					if (patentNumber == null || patentNumber.length() < 3 || word.length() > 3) {
						patentNumber = word;
					}
				} catch (NumberFormatException e) {
					System.out.println("Error in parsing patent : " + e.getMessage());
				}
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("year : " + year + " and patent number : " + patentNumber);
		csv.writeToCSV(text, fileName, year, patentNumber, (endTime - startTime));

	}
}
