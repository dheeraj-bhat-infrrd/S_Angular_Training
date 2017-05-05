/**
 * 
 */
package com.ss.aez.report;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ss.aez.util.PropertyReader;

/**
 * @author Subhrajit
 *
 */
public class WriteResultToExcel {

	public static void writeToExcel(ResultSet rs) throws Exception {
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		XSSFWorkbook workBook = new XSSFWorkbook();
		XSSFSheet sheet = workBook.createSheet("Total Received");
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell;
		int i;
		for (i = 0; i < columnCount; i++) {
			cell = row.createCell(i);
			cell.setCellValue(metaData.getColumnName(i + 1));
		}
		i = 1;
		while (rs.next()) {
			row = sheet.createRow(i);
			for (int j = 0; j < columnCount; j++) {
				cell = row.createCell(j);
				cell.setCellValue(rs.getString(j + 1));
			}
			i++;
		}
		FileOutputStream outStream = new FileOutputStream(
				new File(PropertyReader.getValueForKey("REPORT.OUTPUT.PATH")));
		workBook.write(outStream);
		outStream.close();
		System.out.println("Writing to excel done..");
	}

	public static void writeMultipleResultToExcel(Map<String, ResultSet> resultMap) throws Exception {
		XSSFWorkbook workBook = new XSSFWorkbook();
		for (Entry<String, ResultSet> entry : resultMap.entrySet()) {
			String key = entry.getKey();
			System.out.println("Working for key : " + key);
			try {
				ResultSet resultSet = entry.getValue();
				ResultSetMetaData metaData = resultSet.getMetaData();
				int columnCount = metaData.getColumnCount();
				XSSFSheet sheet = workBook.createSheet(key);

				XSSFRow row = sheet.createRow(0);
				XSSFCell cell;
				int columnIndex;
				for (columnIndex = 0; columnIndex < columnCount; columnIndex++) {
					cell = row.createCell(columnIndex);
					cell.setCellValue(metaData.getColumnName(columnIndex + 1));
				}
				columnIndex = 1;
				while (resultSet.next()) {
					row = sheet.createRow(columnIndex);
					for (int rowIndex = 0; rowIndex < columnCount; rowIndex++) {
						cell = row.createCell(rowIndex);
						cell.setCellValue(resultSet.getString(rowIndex + 1));
					}
					columnIndex++;
				}
				resultSet.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		FileOutputStream outStream = new FileOutputStream(
				new File(PropertyReader.getValueForKey("REPORT.OUTPUT.PATH")));
		workBook.write(outStream);
		outStream.close();
		System.out.println("Writing to excel done..");
	}
}
