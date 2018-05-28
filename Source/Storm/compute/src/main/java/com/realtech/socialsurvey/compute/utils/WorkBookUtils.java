package com.realtech.socialsurvey.compute.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;


public class WorkBookUtils
{
    public static XSSFWorkbook createWorkbook(Map<Integer, List<Object>> data) {
        // Blank workbook
        XSSFWorkbook workBook = new XSSFWorkbook();

        // Create a blank sheet
        XSSFSheet sheet = workBook.createSheet();
        XSSFDataFormat df = workBook.createDataFormat();
        CellStyle style = workBook.createCellStyle();
        style.setDataFormat(df.getFormat("MM/dd/yyyy"));

        // Iterate over data and write to sheet
        Set<Integer> keyset = data.keySet();
        int rownum = 0;
        for (Integer key : keyset) {
            Row row = sheet.createRow(rownum++);
            List<Object> objArr = data.get(key);

            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if (obj instanceof String) {
                    cell.setCellValue((String) obj);
                } else if (obj instanceof Integer) {
                    cell.setCellValue((Integer) obj);
                } else if (obj instanceof Date ) {
                    cell.setCellStyle(style);
                    cell.setCellValue((Date) obj);
                } else if (obj instanceof Long) {
                    cell.setCellValue(String.valueOf((Long) obj));
                } else if (obj instanceof Double) {
                    cell.setCellValue((Double) obj);
                }
            }
        }
        return workBook;
    }

    public static XSSFWorkbook writeToWorkbook(Map<Integer, List<Object>> data, XSSFWorkbook workbook, int enterAt) {
        // USE THE SAME SHEET
        XSSFSheet sheet = workbook.getSheetAt(0);
        // use style from the workbook
        XSSFDataFormat df = workbook.createDataFormat();
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(df.getFormat("MM/dd/yyyy")); // Iterate over data and write to sheet
        Set<Integer> keyset = data.keySet();
        int rownum = enterAt;
        for (Integer key : keyset) {
            Row row = sheet.createRow(rownum++);
            List<Object> objArr = data.get(key);

            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if (obj instanceof String) {
                    cell.setCellValue((String) obj);
                } else if (obj instanceof Integer) {
                    cell.setCellValue((Integer) obj);
                } else if (obj instanceof Date) {
                    cell.setCellStyle(style);
                    cell.setCellValue((Date) obj);
                } else if (obj instanceof Long) {
                    cell.setCellValue(String.valueOf((Long) obj));
                } else if (obj instanceof Double) {
                    cell.setCellValue((Double) obj);
                }
            }
        }
        return workbook;
    }

    public static Map<Integer, List<Object>> writeReportHeader( String headers ) {
        Map<Integer, List<Object>> reportDataToPopulate = new TreeMap<Integer, List<Object>>();
        List<Object> headerList = new ArrayList<Object>();
        String[] headerArr = headers.split(",");
        for(String header : headerArr) {
            headerList.add(header);
        }
        reportDataToPopulate.put(1, headerList);
        return reportDataToPopulate;
    }
}
