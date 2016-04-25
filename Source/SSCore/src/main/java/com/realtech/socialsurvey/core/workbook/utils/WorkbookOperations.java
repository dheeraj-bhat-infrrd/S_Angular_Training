package com.realtech.socialsurvey.core.workbook.utils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;


/**
 * @author RareMile
 *
 */
@Component
public class WorkbookOperations
{

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
}
