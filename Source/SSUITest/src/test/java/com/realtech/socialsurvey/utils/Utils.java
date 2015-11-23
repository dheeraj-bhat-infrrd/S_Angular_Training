package com.realtech.socialsurvey.utils;

import java.io.File;
import java.io.FileFilter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.WebElement;

public class Utils {
	private static SimpleDateFormat humanReadableTime = new SimpleDateFormat(
			"dd MMM yyyy HH:mm:ss.SSS");

	/**
	 * Return the timestamp in a human readable format
	 * @return
	 */
	public static String getHumanReadableTimeStamp() {
		return humanReadableTime.format(Calendar.getInstance().getTime());
	}
	
	private static SimpleDateFormat serverDateFormat = new SimpleDateFormat(
			"yyyy/MM/dd");

	/**
	 * Check if the list is sorted in ascending order
	 * 
	 * @param arr
	 * @return
	 */
	public static boolean isAscending(List<WebElement> arr) {
		
		boolean isSorted = false;
		for (int i = 0; i < arr.size() - 1; i++) {
			System.err.println(arr.get(i+1).getText() + " : " + arr.get(i).getText());
			String tempString1 = arr.get(i).getText().substring(0,arr.get(i).getText().indexOf("["));
			String tempString2 = arr.get(i+1).getText().substring(0,arr.get(i+1).getText().indexOf("["));
			
			
			int ascendingNo =tempString2.compareToIgnoreCase(tempString1);
			if(ascendingNo >= 0){
				System.out.println("ascending : " +ascendingNo);
				isSorted = true;
			} else {
				System.out.println("desc : "+ascendingNo);
				return false;
			}
		}
		return isSorted;
	}
	
	/**
	 * Check if the list is sorted in descending order
	 * 
	 * @param arr
	 * @return
	 */
	public static boolean isDescending(List<WebElement> arr) {
		System.err.println("elements :  " +arr.size());
		boolean isSorted = false;
		for (int i = 0; i < arr.size() - 1; i++) {
			System.err.println(i +": arr.get(i).getText()  : " + arr.get(i).getText());
		}
		for (int i = 0; i < arr.size() - 1; i++) {
			System.out.println(i +": arr.get(i).getText()  : " + arr.get(i).getText());
			String tempString1 = arr.get(i).getText().substring(0,arr.get(i).getText().indexOf("["));
			System.out.println((i+1) +" : arr.get(i+1).getText()  : " + arr.get(i+1).getText());
			String tempString2 = arr.get(i+1).getText().substring(0,arr.get(i+1).getText().indexOf("["));
			System.err.println(tempString1 + " : " + tempString2);
			int ascendingNo = tempString2.compareToIgnoreCase(tempString1);
			if(ascendingNo <= 0){
				System.out.println("desc : " +ascendingNo);
				isSorted = true;
			} else {
				System.out.println("asc : "+ascendingNo);
				return false;
			}
		}
		return isSorted;
	}
	
	public static void main(String[] args) {
		System.out.println("test[v1]".replaceAll("\\[[\\w\\d]+\\]", ""));
		System.out.println("test[v1]".substring(0,"test[v1]".indexOf("[")));
	}
	
	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days); // minus number would decrement the days
		return cal.getTime();
	}
	
	public static String getDateString(Date date){
		String dateString= serverDateFormat.format(date);
		return dateString;
	}

	public static Date getDateObject(String dateString){   
		Date date = null;
		try {
			date = serverDateFormat.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * Method is for getting last modified file which recently exported.
	 * 
	 * @param dir
	 *            file directory where we have to search for download file
	 * @return
	 */
	public static File lastFileModified(String dir) {
		File fl = new File(dir);
		File[] files = fl.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		});
		long lastMod = Long.MIN_VALUE;
		File choise = null;
		for (File file : files) {
			if (file.lastModified() > lastMod) {
				choise = file;
				lastMod = file.lastModified();
			}
		}
		return choise;
	}
	
	/**
	 * Method is for getting file size in KB
	 * @param file to find size in KB
	 * @return
	 */
	public static double getFileSize(File file) {
		double fileSize = (double) file.length()/1024;
		BigDecimal a = new BigDecimal(fileSize);
		BigDecimal roundOff = a.setScale(10, BigDecimal.ROUND_HALF_EVEN);
		return roundOff.doubleValue();
	}
	
}
