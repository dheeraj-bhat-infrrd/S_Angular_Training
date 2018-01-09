package com.realtech.socialsurvey.core.workbook.utils;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


/**
 * @author RareMile
 *
 */
@Component
public class WorkbookOperations
{

    private static final DataFormatter dataFormatter = new DataFormatter();

    /**
     * Method to create workbook
     * 
     * @param data
     * @return XSSFWorkbook
     */
    public XSSFWorkbook createWorkbook( Map<Integer, List<Object>> data )
    {
        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Create a blank sheet
        XSSFSheet sheet = workbook.createSheet();
        XSSFDataFormat df = workbook.createDataFormat();
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat( df.getFormat( CommonConstants.DATE_FORMAT ) );

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
        return workbook;
    }
    

    /**
     * @param data
     * @param workbook
     * @param enterAt
     * @return
     */
    public XSSFWorkbook writeToWorkbook( Map<Integer, List<Object>> data , XSSFWorkbook workbook , int enterAt)
    {
        //USE THE SAME SHEET 
        XSSFSheet sheet = workbook.getSheetAt( 0 );
        //use style from the workbook
        XSSFDataFormat df = workbook.createDataFormat();
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat( df.getFormat( CommonConstants.DATE_FORMAT ) );        // Iterate over data and write to sheet
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


    
    public XSSFWorkbook createWorkbook( Map<Integer, List<Object>> data, DecimalFormat decimalFormat )
    {
        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Create a blank sheet
        XSSFSheet sheet = workbook.createSheet();
        XSSFDataFormat df = workbook.createDataFormat();
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat( df.getFormat( CommonConstants.DATE_FORMAT ) );

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
                    cell.setCellValue( decimalFormat.format( obj ) );
                }
            }
        }
        return workbook;
    }


    public XSSFWorkbook createWorkbook( Map<Integer, List<Object>> data, String dateFormat )
    {
        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Create a blank sheet
        XSSFSheet sheet = workbook.createSheet();
        XSSFDataFormat df = workbook.createDataFormat();
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat( df.getFormat( dateFormat ) );

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
        return workbook;
    }


    /**
     * method to trim all strings in a given workbook 
     * @param workBook
     * @return XSSFWorkbook
     * @throws InvalidInputException
     */
    public void trimWorkBook( XSSFWorkbook workBook ) throws InvalidInputException
    {
        if ( workBook == null || workBook.getNumberOfSheets() == 0 )
            throw new InvalidInputException( "Empty workbook" );

        //iterate through the sheets
        for ( int iteratorValue = 0; iteratorValue < workBook.getNumberOfSheets(); iteratorValue++ ) {

            //iterate through the rows
            XSSFSheet sheet = workBook.getSheetAt( iteratorValue );
            parseWorkBookRows( sheet.rowIterator() );
        }
    }


    private void parseWorkBookRows( Iterator<Row> rows )
    {
        while ( rows.hasNext() ) {

            //iterate through the cells
            XSSFRow row = (XSSFRow) rows.next();
            parseCells( row.cellIterator() );

        }
    }


    private void parseCells( Iterator<Cell> cells )
    {
        while ( cells.hasNext() ) {

            // trim string values in each cell
            XSSFCell cell = (XSSFCell) cells.next();
            if ( cell.getCellType() == Cell.CELL_TYPE_STRING ) {
                cell.setCellValue( cell.getStringCellValue().trim() );
            }
        }
    }
    
    public String getStringValue( Cell cell ){
        if( cell == null ){
            return "";
        } else {
            return StringUtils.trimToEmpty( dataFormatter.formatCellValue( cell ) );
        }
    }

}
