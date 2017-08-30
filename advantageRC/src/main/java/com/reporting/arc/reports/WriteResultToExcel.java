/**
 * 
 */
package com.reporting.arc.reports;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.reporting.arc.utils.PropertyReader;


/**
 * @author Subhrajit
 *
 */
public class WriteResultToExcel
{

    public static void writeToExcel( ResultSet rs ) throws Exception
    {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        XSSFWorkbook workBook = new XSSFWorkbook();
        XSSFSheet sheet = workBook.createSheet( "Total Received" );
        XSSFRow row = sheet.createRow( 0 );
        XSSFCell cell;
        int i;
        for ( i = 0; i < columnCount; i++ ) {
            cell = row.createCell( i );
            cell.setCellValue( metaData.getColumnName( i + 1 ) );
        }
        i = 1;
        while ( rs.next() ) {
            row = sheet.createRow( i );
            for ( int j = 0; j < columnCount; j++ ) {
                cell = row.createCell( j );
                cell.setCellValue( rs.getString( j + 1 ) );
            }
            i++;
        }
        FileOutputStream outStream = new FileOutputStream( new File( PropertyReader.getValueForKey( "REPORT.OUTPUT.PATH" ) ) );
        workBook.write( outStream );
        outStream.close();
        System.out.println( "Writing to excel done.." );
    }


    @SuppressWarnings ( "static-access")
    public static void writeMultipleResultToExcel( Map<String, ResultSet> resultMap ) throws Exception
    {
        XSSFWorkbook workBook = new XSSFWorkbook();
        //trying to make sheet TOTAL_RECEIVED as default
        String main_key = "TOTAL_RECEIVED";
        System.out.println( "Working for key : " + main_key );
        ResultSet main_resultSet = resultMap.get( main_key );
        ResultSetMetaData main_metaData = main_resultSet.getMetaData();
        String main_index[] = { "PENDING", "TOTAL_DUPLICATES", "TOTAL_CORRUPTED", "TOTAL_ABUSIVE", "OLD_RECORDS",
            "TOTAL_IGNORED", "TOTAL_UNASSIGNED", "TOTAL_SENT", "TOTAL_SURVEYS_CLICKED", "TOTAL_COMPLETED",
            "TOTAL_PARTIALLY_COMPLETED", "COMPLETE_PERCENTAGE", "Delta" };
        int basic_columnCount = main_metaData.getColumnCount(); // returns 5 
        int main_indCount = main_index.length; //returns 13
        int main_columnCount = basic_columnCount + main_indCount;
        XSSFSheet main_sheet = workBook.createSheet( main_key );
        XSSFRow main_row = main_sheet.createRow( 0 );
        XSSFCell main_cell;
        //creating a cell style make header bold 
        CellStyle style = workBook.createCellStyle();
        XSSFFont font = workBook.createFont();
        font.setBoldweight( XSSFFont.BOLDWEIGHT_BOLD );
        style.setFont( font );
        //use the above to make column index bold 
        int receive_columnIndex;
        int array_colIndex = 0;
        int temp_count = basic_columnCount;
        //create a list of the first column 
        List<String> list_main_id = new ArrayList<>();
        for ( receive_columnIndex = 0; receive_columnIndex < main_columnCount; receive_columnIndex++ ) {
            if ( temp_count > 0 ) {
                main_cell = main_row.createCell( receive_columnIndex );
                main_cell.setCellStyle( style );
                main_cell.setCellValue( main_metaData.getColumnName( receive_columnIndex + 1 ) );
                //add index to list
                if ( receive_columnIndex == 0 ) {
                    list_main_id.add( main_metaData.getColumnName( receive_columnIndex + 1 ) );
                }
                temp_count--;
            } else if ( array_colIndex < main_indCount ) {
                main_cell = main_row.createCell( receive_columnIndex );
                main_cell.setCellStyle( style );
                main_cell.setCellValue( main_index[array_colIndex] );
                array_colIndex++;
            }

        }
        receive_columnIndex = 1;
        while ( main_resultSet.next() ) {
            main_row = main_sheet.createRow( receive_columnIndex );
            for ( int rowIndex = 0; rowIndex < basic_columnCount; rowIndex++ ) {
                main_cell = main_row.createCell( rowIndex );
                if ( rowIndex == 0 ) {
                    list_main_id.add( main_resultSet.getString( rowIndex + 1 ) );
                    main_cell.setCellValue( main_resultSet.getString( rowIndex + 1 ) );
                } else if ( rowIndex == 1 ) {
                   String company = main_resultSet.getString( rowIndex + 1 );
                    if ( company.equals( "Advantage Rent A Car" ) ) {
                        main_cell.setCellValue( "ADV" );
                    } else if ( company.equals( "E-Z Rent-a-Car" ) ) {
                        main_cell.setCellValue( "EZ" );
                    } else {
                        main_cell.setCellValue( main_resultSet.getString( rowIndex + 1 ) );
                    } 
                } else if ( rowIndex == 3 ) {
                    String date = main_resultSet.getString( rowIndex + 1 );
                    String year = date.substring( 0, 4 );
                    String month = date.substring( 4, 6 );
                    main_cell.setCellValue( year + "-" + month );
                } else if ( rowIndex == 4 ) {
                    //converting string to int
                    int conv = Integer.parseInt( main_resultSet.getString( rowIndex + 1 ) );
                    main_cell.setCellValue( conv );
                    main_cell.setCellType( main_cell.CELL_TYPE_NUMERIC );
                } else {
                    main_cell.setCellValue( main_resultSet.getString( rowIndex + 1 ) );
                }
            }
            receive_columnIndex++;
        }

        for ( Entry<String, ResultSet> entry : resultMap.entrySet() ) {
            String key = entry.getKey();
            if ( !key.equals( "TOTAL_RECEIVED" ) ) { //excluding default sheet
                System.out.println( "Working for key : " + key );
                try {
                    //finding the column number on main page
                    int keyPositionMain = Arrays.asList( main_index ).indexOf( key ) + basic_columnCount;
                    int rowNumber = 0;
                    //System.out.println("key present at  : " + keyPositionMain);//to check if postion was right 
                    ResultSet resultSet = entry.getValue();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    XSSFSheet sheet = workBook.createSheet( key );

                    XSSFRow row = sheet.createRow( 0 );
                    XSSFCell cell;
                    int columnIndex;
                    for ( columnIndex = 0; columnIndex < columnCount; columnIndex++ ) {
                        cell = row.createCell( columnIndex );
                        cell.setCellStyle( style );
                        cell.setCellValue( metaData.getColumnName( columnIndex + 1 ) );
                    }
                    columnIndex = 1;
                    String Id;
                    int count=1;
                    while ( resultSet.next() ) {
                        row = sheet.createRow( columnIndex );
                        for ( int rowIndex = 0; rowIndex < columnCount; rowIndex++ ) {
                            cell = row.createCell( rowIndex );
                            if ( rowIndex == 0 ) {
                                cell.setCellValue( resultSet.getString( rowIndex + 1 ) );
                                Id = resultSet.getString( rowIndex + 1 );
                                //diff between a list an excel so we add one to the index 
                                //getting the row number
                                rowNumber = list_main_id.indexOf( Id );
                                System.out.println( resultSet.getString( rowIndex + 1 ) +"+"+ rowNumber );
                            } else if ( rowIndex == 1 ) {
                                String company = resultSet.getString( rowIndex + 1 );
                                if ( company.equals( "Advantage Rent A Car" ) ) {
                                    cell.setCellValue( "ADV" );
                                }else if ( company.equals( "E-Z Rent-a-Car" ) ) {
                                    cell.setCellValue( "EZ" );
                                }  else {
                                    cell.setCellValue( main_resultSet.getString( rowIndex + 1 ) );
                                }
                            } else if ( rowIndex == 3 ) {
                                String date = resultSet.getString( rowIndex + 1 );
                                String year = date.substring( 0, 4 );
                                String month = date.substring( 4, 6 );
                                cell.setCellValue( year + "-" + month );
                            } else if ( rowIndex == 4 ) {
                                //converting string to int
                                int conv = Integer.parseInt( resultSet.getString( rowIndex + 1 ) );
                                cell.setCellValue( conv );
                                cell.setCellType( cell.CELL_TYPE_NUMERIC );
                                main_row = main_sheet.getRow( rowNumber );
                                main_cell = main_row.createCell( keyPositionMain );
                                main_cell.setCellValue( conv );
                                main_cell.setCellType( main_cell.CELL_TYPE_NUMERIC );
                            } else {
                                cell.setCellValue( resultSet.getString( rowIndex + 1 ) );
                            }
                        }
                        columnIndex++;
                        count++;
                    }
                    System.out.println( count );
                    resultSet.close();
                } catch ( Exception e ) {
                    System.out.println( e.getMessage() );
                }

            }

        
        }
        main_resultSet.close();
        
        FileOutputStream outStream = new FileOutputStream( PropertyReader.getValueForKey( "REPORT.OUTPUT.PATH" )  );
        workBook.write( outStream );
        outStream.close();
        System.out.println( "Writing to excel done.." );
    }

}
