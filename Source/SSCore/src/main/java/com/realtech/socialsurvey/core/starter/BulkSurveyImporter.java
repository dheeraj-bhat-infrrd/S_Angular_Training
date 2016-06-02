package com.realtech.socialsurvey.core.starter;

import com.realtech.socialsurvey.core.entities.SurveyImportVO;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class BulkSurveyImporter extends QuartzJobBean
{
    private static String fileName = "./surveyimport.xlsx";
    public static final Logger LOG = LoggerFactory.getLogger( BillingReportGenerator.class );

    private static final int USER_ID_INDEX = 2;
    private static final int CUSTOMER_FIRSTNAME_INDEX = 4;
    private static final int CUSTOMER_LASTNAME_INDEX = 5;
    private static final int CUSTOMER_EMAIL_INDEX = 6;
    private static final int SURVEY_COMPLETION_INDEX = 7;
    private static final int SCORE_INDEX = 8;
    private static final int COMMENT_INDEX = 9;

    private SurveyHandler surveyHandler;

    /**
     * Method to read the csv file and get a list of SurveyImportVO objects
     * @return
     * @throws InvalidInputException
     */
    private List<SurveyImportVO> getSurveyListFromCsv() throws InvalidInputException
    {
        LOG.info( "BulkSurveyImporter.getSurveyListFromCsv started" );
        InputStream fileStream = null;
        List<SurveyImportVO> surveyList = new ArrayList<>();
        try {
            fileStream = new FileInputStream( fileName );
            if ( fileStream == null )
                return null;
            XSSFWorkbook workBook = new XSSFWorkbook( fileStream );
            XSSFSheet regionSheet = workBook.getSheetAt( 0 );
            Iterator<Row> rows = regionSheet.rowIterator();
            Iterator<Cell> cells = null;
            XSSFRow row = null;
            XSSFCell cell = null;
            while ( rows.hasNext() ) {
                row = (XSSFRow) rows.next();
                // skip the first 1 row for the header
                if ( row.getRowNum() < 1 ) {
                    continue;
                }
                SurveyImportVO survey = new SurveyImportVO();
                LOG.info( "Processing row " + row.getRowNum() + " from the file." );
                cells = row.cellIterator();
                while ( cells.hasNext() ) {
                    cell = (XSSFCell) cells.next();
                    if ( cell.getColumnIndex() == USER_ID_INDEX ) {
                        if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC && !String
                            .valueOf( (long) cell.getNumericCellValue() ).trim().isEmpty() ) {
                            survey.setUserId( (long) cell.getNumericCellValue() );
                        } else if ( !cell.getStringCellValue().trim().isEmpty() ) {
                            survey.setUserId( Long.parseLong( cell.getStringCellValue().trim() ) );
                        }
                    } else if ( cell.getColumnIndex() == CUSTOMER_FIRSTNAME_INDEX && !cell.getStringCellValue().trim()
                        .isEmpty() ) {
                        survey.setCustomerFirstName( cell.getStringCellValue().trim() );
                    } else if ( cell.getColumnIndex() == CUSTOMER_LASTNAME_INDEX && !cell.getStringCellValue().trim()
                        .isEmpty() ) {
                        survey.setCustomerLastName( cell.getStringCellValue().trim() );
                    } else if ( cell.getColumnIndex() == CUSTOMER_EMAIL_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                        survey.setCustomerEmailAddress( cell.getStringCellValue().trim() );
                    } else if ( cell.getColumnIndex() == SURVEY_COMPLETION_INDEX ) {
                        survey.setSurveyDate( cell.getDateCellValue() );
                    } else if ( cell.getColumnIndex() == SCORE_INDEX ) {
                        survey.setScore( cell.getNumericCellValue() );
                    } else if ( cell.getColumnIndex() == COMMENT_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                        survey.setReview( cell.getStringCellValue() );
                    }
                }
                try {
                    validateSurveyRow( survey );
                } catch ( InvalidInputException e ) {
                    throw new InvalidInputException(
                        "Error occurred at row : " + row.getRowNum() + ". Reason : " + e.getMessage() );
                }
                surveyList.add( survey );
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        LOG.info( "BulkSurveyImporter.getSurveyListFromCsv finished" );
        return surveyList;
    }


    private static void validateSurveyRow( SurveyImportVO survey ) throws InvalidInputException
    {
        if ( survey.getUserId() <= 0 )
            throw new InvalidInputException( "Invalid userId : " + survey.getUserId() );
        if ( survey.getCustomerEmailAddress() == null || survey.getCustomerEmailAddress().isEmpty() )
            throw new InvalidInputException( "Customer Email Address cannot be empty" );
        if ( survey.getCustomerFirstName() == null || survey.getCustomerFirstName().isEmpty() )
            throw new InvalidInputException( "Customer First Name cannot be empty" );
        if ( survey.getReview() == null || survey.getReview().isEmpty() )
            throw new InvalidInputException( "Review cannot be empty" );
        if ( survey.getSurveyDate() == null )
            throw new InvalidInputException( "Survey date cannot be empty" );
        if ( survey.getScore() < 0.0 || survey.getScore() > 5.0 )
            throw new InvalidInputException( "Invalid survey score : " + survey.getScore() );
    }


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        LOG.info( "Bulk Survey Importer started" );
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        try {
            List<SurveyImportVO> surveyImportVOs = getSurveyListFromCsv();
            if ( surveyImportVOs != null && !surveyImportVOs.isEmpty() ) {
                for ( SurveyImportVO surveyImportVO : surveyImportVOs ) {
                    surveyHandler.importSurveyVOToDBs( surveyImportVO );
                }
            }
        } catch ( NonFatalException e ) {
            LOG.error( "An error occurred while uploading the surveys. Reason: ", e );
        }
        LOG.info( "Bulk Survey Importer finished" );
    }

    private void initializeDependencies( JobDataMap jobMap )
    {
        surveyHandler = (SurveyHandler) jobMap.get( "surveyHandler" );
    }


}
