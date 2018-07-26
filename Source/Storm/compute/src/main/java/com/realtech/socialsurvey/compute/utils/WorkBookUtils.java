package com.realtech.socialsurvey.compute.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class WorkBookUtils
{
    public static XSSFWorkbook createWorkbook( Map<Integer, List<Object>> data )
    {
        // Blank workbook
        XSSFWorkbook workBook = new XSSFWorkbook();

        // Create a blank sheet
        XSSFSheet sheet = workBook.createSheet();
        XSSFDataFormat df = workBook.createDataFormat();
        CellStyle style = workBook.createCellStyle();
        style.setDataFormat( df.getFormat( "MM/dd/yyyy" ) );

        // Iterate over data and write to sheet
        Set<Integer> keyset = data.keySet();
        int rownum = 0;
        for ( Integer key : keyset ) {
            Row row = sheet.createRow( rownum++ );
            List<Object> objArr = data.get( key );

            int cellnum = 0;
            for ( Object obj : objArr ) {
                Cell cell = row.createCell( cellnum++ );
                if ( obj instanceof String ) {
                    cell.setCellValue( (String) obj );
                } else if ( obj instanceof Integer ) {
                    cell.setCellValue( (Integer) obj );
                } else if ( obj instanceof Date ) {
                    cell.setCellStyle( style );
                    cell.setCellValue( (Date) obj );
                } else if ( obj instanceof Long ) {
                    cell.setCellValue( String.valueOf( (Long) obj ) );
                } else if ( obj instanceof Double ) {
                    cell.setCellValue( (Double) obj );
                }
            }
        }
        return workBook;
    }


    public static XSSFWorkbook writeToWorkbook( Map<Integer, List<Object>> data, XSSFWorkbook workbook, int enterAt )
    {
        // USE THE SAME SHEET
        XSSFSheet sheet = workbook.getSheetAt( 0 );
        // use style from the workbook
        XSSFDataFormat df = workbook.createDataFormat();
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat( df.getFormat( "MM/dd/yyyy" ) ); // Iterate over data and write to sheet
        Set<Integer> keyset = data.keySet();
        int rownum = enterAt;
        for ( Integer key : keyset ) {
            Row row = sheet.createRow( rownum++ );
            List<Object> objArr = data.get( key );

            int cellnum = 0;
            for ( Object obj : objArr ) {
                Cell cell = row.createCell( cellnum++ );
                if ( obj instanceof String ) {
                    cell.setCellValue( (String) obj );
                } else if ( obj instanceof Integer ) {
                    cell.setCellValue( (Integer) obj );
                } else if ( obj instanceof Date ) {
                    cell.setCellStyle( style );
                    cell.setCellValue( (Date) obj );
                } else if ( obj instanceof Long ) {
                    cell.setCellValue( String.valueOf( (Long) obj ) );
                } else if ( obj instanceof Double ) {
                    cell.setCellValue( (Double) obj );
                }
            }
        }
        return workbook;
    }


    public static Map<Integer, List<Object>> writeReportHeader( String headers )
    {
        Map<Integer, List<Object>> reportDataToPopulate = new TreeMap<Integer, List<Object>>();
        List<Object> headerList = new ArrayList<Object>();
        String[] headerArr = headers.split( "," );
        for ( String header : headerArr ) {
            headerList.add( header );
        }
        reportDataToPopulate.put( 1, headerList );
        return reportDataToPopulate;
    }


    public static XSSFWorkbook updateWorkBook( Map<Integer, List<Object>> rows, XSSFWorkbook workbook, String sheetName,
        int index, String dataFormat, boolean setBold, boolean setWrap, int columnWidth, boolean hCenter, boolean vCenter )
    {

        XSSFSheet sheet = null;
        CellStyle formatStyle = null;
        CellStyle rowStyle = null;

        if ( workbook == null ) {
            workbook = new XSSFWorkbook();
        }

        if ( StringUtils.isEmpty( sheetName ) ) {
            sheet = workbook.createSheet();
        } else {
            if ( workbook.getSheet( sheetName ) == null ) {
                workbook.createSheet( sheetName );
            }
            sheet = workbook.getSheet( sheetName );
        }

        if ( StringUtils.isNotEmpty( dataFormat ) ) {
            formatStyle = workbook.createCellStyle();
            XSSFDataFormat df = workbook.createDataFormat();
            formatStyle.setDataFormat( df.getFormat( dataFormat ) );
        }


        if ( setWrap ) {
            rowStyle = workbook.createCellStyle();
            rowStyle.setWrapText( true );
        }

        if ( setBold ) {

            if ( rowStyle == null ) {
                rowStyle = workbook.createCellStyle();
            }

            XSSFFont boldFont = workbook.createFont();
            boldFont.setBold( true );
            rowStyle.setFont( boldFont );
        }

        if ( columnWidth > 0 ) {
            sheet.setDefaultColumnWidth( columnWidth );
        }

        if ( hCenter ) {
            rowStyle.setAlignment( CellStyle.ALIGN_CENTER );
        }

        if ( vCenter ) {
            rowStyle.setVerticalAlignment( CellStyle.VERTICAL_CENTER );
        }


        if ( rows != null ) {
            Set<Integer> keyset = rows.keySet();
            int rownum = index >= 0 ? index : sheet.getLastRowNum();

            for ( Integer key : keyset ) {
                Row row = sheet.createRow( rownum++ );
                List<Object> objArr = rows.get( key );

                int cellnum = 0;
                for ( Object obj : objArr ) {
                    Cell cell = row.createCell( cellnum++ );

                    if ( rowStyle != null ) {
                        cell.setCellStyle( rowStyle );
                    }

                    if ( obj instanceof String ) {
                        cell.setCellValue( (String) obj );
                    } else if ( obj instanceof Integer ) {
                        cell.setCellValue( (Integer) obj );
                    } else if ( obj instanceof Date ) {
                        if ( formatStyle != null ) {
                            cell.setCellStyle( formatStyle );
                        }
                        cell.setCellValue( (Date) obj );
                    } else if ( obj instanceof Long ) {
                        cell.setCellValue( String.valueOf( (Long) obj ) );
                    } else if ( obj instanceof Double ) {
                        cell.setCellValue( (Double) obj );
                    }
                }
            }
        }
        return workbook;
    }
}
