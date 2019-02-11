package com.realtech.socialsurvey.compute.topology.bolts.transactioningestion;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.RetrofitApiBuilder;
import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.BulkSurveyPutVO;
import com.realtech.socialsurvey.compute.entities.ServiceProviderInfo;
import com.realtech.socialsurvey.compute.entities.SurveyPutVO;
import com.realtech.socialsurvey.compute.entities.TransactionInfoPutVO;
import com.realtech.socialsurvey.compute.entities.TransactionIngestionMessage;
import com.realtech.socialsurvey.compute.entities.TransactionSourceFtp;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;

import retrofit2.Call;
import retrofit2.Response;


public class ConvertToSurveyObject extends BaseComputeBoltWithAck
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( ConvertToSurveyObject.class );
    private static final String[] MANDATORY_FIELDS = { "bor1Email", "bor1FirstName", "servicer", "serviceremail", "fileName" };
    private static final String UTF8_BOM = "\uFEFF";
	private static final List<String> DATE_FORMATS = Arrays.asList("MM-dd-yy HH:mm:ss.SSS", "MM-dd-yy", "MM/dd/yy",
			"MM/dd/yy HH:mm:ss.SSS", "MM-dd-yyyy HH:mm:ss.SSS", "MM-dd-yyyy", "MM/dd/yyyy", "MM/dd/yyyy HH:mm:ss.SSS",
			"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd" );
    
    private static final String SYSTEM_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private List<String> UNMATCHED_FILE_HEADERS  = null;

    @Override
    public void declareOutputFields( OutputFieldsDeclarer outputFieldsDeclarer )
    {
        //need to discuss what the output of the spout needs to be 
        outputFieldsDeclarer.declare( new Fields( "isSuccess", "transactionIngestionMessage", "bulkSurvey" ) );
    }


    @Override
    public void executeTuple( Tuple input )
    {
        LOG.debug( "Executing convert to survey object bolt." );
        boolean prevSucccess = input.getBooleanByField( "isSuccess" );
        boolean success = false;
        BulkSurveyPutVO bulkSurveyPutVO = null;
        List<SurveyPutVO> surveyList = new ArrayList<>();
        TransactionIngestionMessage transactionIngestionMessage = (TransactionIngestionMessage) input
            .getValueByField( "transactionIngestionMessage" );
        long companyId = transactionIngestionMessage.getCompanyId();
        long ftpId = transactionIngestionMessage.getFtpId();
        if ( prevSucccess ) {
            Path targetPath = (Path) input.getValueByField( "targetPath" );
            try {
                TransactionSourceFtp transactionSourceFtp = fetchFtpData( companyId, ftpId );
                LOG.debug( "the transactionSourceFtp is : {}", transactionSourceFtp );
                if ( transactionSourceFtp != null ) {
                   surveyList = createSurveyList(targetPath, transactionSourceFtp.getFileHeaderMapper(), transactionIngestionMessage);
                    LOG.debug( "the surveyList is : {}", surveyList );
                    if ( surveyList == null ) {
                        LOG.warn( "The required system mandatory header's were not mapped" );
                        SSAPIOperations.getInstance().processFailedFtpRequest( createErrorForHeaders(),
                            transactionIngestionMessage, false );
                    } else if ( !surveyList.isEmpty() ) {
                        bulkSurveyPutVO = convertToBulk( surveyList, companyId, transactionSourceFtp.getFtpSource() );
                        success = true;
                    } else {
                        LOG.warn( "There is no transaction data in the file" );
                        SSAPIOperations.getInstance().processFailedFtpRequest( "There is no transaction data in the file",
                            transactionIngestionMessage, false );
                    }
                }
            } catch ( IOException e ) {
                LOG.error( "Failed to convert to survey object targetPath: {} on local", targetPath );
            }
            LOG.info( "Emitting tuple with success = {} , transactionIngestionMessage = {}, surveyList = {}", success,
                targetPath, surveyList );
            _collector.emit( input, Arrays.asList( success, transactionIngestionMessage, bulkSurveyPutVO ) );
        }
    }


    @Override
    public List<Object> prepareTupleForFailure()
    {
        return Arrays.asList( false, null, null );
    }


    private String getExtention( Path targetPath )
    {
        String extension = "";
        Path fileName = targetPath.getFileName();
        int dotIndex = fileName.toString().lastIndexOf( '.' );
        if ( dotIndex >= 0 ) {
            extension = fileName.toString().substring( dotIndex + 1 );
        }
        LOG.info( "The extention of file {}", extension );
        return extension;
    }


    private List<SurveyPutVO> readFromCsv( String csvFileURI, Map<String, String> fileHeaderMapper ) throws IOException, ParseException
    {

        LOG.debug( "method readFromCsv started" );

        List<SurveyPutVO> surveyList = new ArrayList<>();
        Map<String, Integer> actualFileHeader = new HashMap<>();
        String dateFormat = null;
        String systemDate = null;
        int lineNumber = 0;

        try ( BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( csvFileURI ) ) ) ) {

            String line = null;
            boolean start = true;
            


            // read all the lines form the source and extract the fields
            while ( ( line = reader.readLine() ) != null ) {

                // process all the lines and retrieve the trimmed data
                String[] entries = line.split( ",", -1 );
                if ( start ) {
                    line = removeUTF8BOM(line);
                    actualFileHeader = actualHeader( new ArrayList<>(Arrays.asList(line.split( "," ))), fileHeaderMapper );
                    if ( actualFileHeader == null ) {
                        LOG.debug( "mandatory feilds don't exist for file : {}", csvFileURI );
                        //Find the headers which weren't matched with the system
                        intersectionHeaders(new ArrayList<>(Arrays.asList(line.split( "," ))),fileHeaderMapper.values());
                        //return null to differentiate if it failed because of headers
                        return null;
                    }
                    ++lineNumber;
                    start = false;
                } else if(ifEntryNotEmpty(new ArrayList<>(Arrays.asList(entries)))){

                    if ( actualFileHeader.containsKey( "fundedDate" ) ) {
                        String actualFileDate = entries[actualFileHeader.get( "fundedDate" )];

                        if ( dateFormat == null ) {
                            dateFormat = multipleDateFormat( DATE_FORMATS, actualFileDate );
                        }

                        Date fileDate = new SimpleDateFormat( dateFormat ).parse( actualFileDate );
                        systemDate = new SimpleDateFormat( SYSTEM_DATE_FORMAT ).format( fileDate );


                    } else
                        systemDate = new SimpleDateFormat( SYSTEM_DATE_FORMAT ).format( Calendar.getInstance().getTime() );
                    surveyList.add( creatingSurveyObject(new ArrayList<>(Arrays.asList(entries)), actualFileHeader, systemDate, ++lineNumber) );
                    
                } else {
                    surveyList.add(creatingEmptySurveyObject(++lineNumber));
                }

            }

        } catch ( IOException readError ) {
            LOG.error( "Unable read CSV file." );
            throw new IOException( "Unable to read Csv from the source" );
        }
        return surveyList;
    }
    

    private List<SurveyPutVO> readFromXlsx( String xlsxFileURI, Map<String, String> fileHeaderMapper )
        throws IOException, ParseException
    {
        LOG.debug( "method to read an xlsx" );
        List<SurveyPutVO> surveyList = new ArrayList<>();
        Map<String, Integer> actualFileHeader = new HashMap<>();
        String dateFormat = null;
        boolean start = true;
        String systemDate = null;

        //input the file
        InputStream excelFileToRead = new FileInputStream( xlsxFileURI );
        XSSFWorkbook wb = new XSSFWorkbook( excelFileToRead );
        XSSFSheet sheet = wb.getSheetAt( 0 );
        XSSFRow row;
        Iterator rows = sheet.rowIterator();
        int lineNumber = 0;
        while ( lineNumber < sheet.getPhysicalNumberOfRows() ) {
            row = sheet.getRow( lineNumber );
            if(row == null) {
                surveyList.add(creatingEmptySurveyObject(++lineNumber));
                continue;
            }
            ArrayList<String> entries = readCompleteXSSFRow(row);

            if ( start ) {
                actualFileHeader = actualHeader( entries, fileHeaderMapper );
                if ( actualFileHeader == null ) {
                    LOG.debug( "mandatory feilds don't exist for file : {}", xlsxFileURI );
                    //Find the headers which weren't matched with the system
                    intersectionHeaders( entries, fileHeaderMapper.values() );
                    //return null to differentiate if it failed because of headers
                    return null;
                }
                ++lineNumber;
                start = false;
            } else if(ifEntryNotEmpty(entries)){

                if ( actualFileHeader.containsKey( "fundedDate" ) ) {
                    String actualFileDate = entries.get( actualFileHeader.get( "fundedDate" ) );

                    if ( dateFormat == null ) {
                        dateFormat = multipleDateFormat( DATE_FORMATS, actualFileDate );
                    }

                    Date fileDate = new SimpleDateFormat( dateFormat ).parse( actualFileDate );
                    systemDate = new SimpleDateFormat( SYSTEM_DATE_FORMAT ).format( fileDate );


                } else
                    systemDate = new SimpleDateFormat( SYSTEM_DATE_FORMAT ).format( Calendar.getInstance().getTime() );


                surveyList.add( creatingSurveyObject( entries, actualFileHeader, systemDate, ++lineNumber ) );

            } else {
                surveyList.add(creatingEmptySurveyObject(++lineNumber));
            }
            
        }
        return surveyList;
    }


    private ArrayList<String> readCompleteXSSFRow( XSSFRow row )
    {
        ArrayList<String> entries = new ArrayList<>();
        for ( int i = 0; i < row.getLastCellNum(); i++ ) {
            Cell cell = row.getCell( i );
            if ( cell == null ) {
                entries.add( "" );
            } else {
                entries.add( new DataFormatter().formatCellValue( cell ) );
            }
        }
        return entries;
    }
    
    private List<SurveyPutVO> readFromXls( String xlsFileURI, Map<String, String> fileHeaderMapper )
        throws IOException, ParseException
    {
        LOG.debug( "method to read an xls" );
        List<SurveyPutVO> surveyList = new ArrayList<>();
        Map<String, Integer> actualFileHeader = new HashMap<>();
        String dateFormat = null;
        boolean start = true;
        String systemDate = null;

        //input the file
        InputStream excelFileToRead = new FileInputStream( xlsFileURI );
        HSSFWorkbook hb = new HSSFWorkbook( excelFileToRead );
        HSSFSheet sheet = hb.getSheetAt( 0 );
        HSSFRow row;
        Iterator rows = sheet.rowIterator();
        //keeping line count since we are skipping empty rows
        int lineNumber = 0;
        while ( lineNumber < sheet.getPhysicalNumberOfRows() ) {
            row = sheet.getRow( lineNumber );
            if ( row == null ) {
                surveyList.add( creatingEmptySurveyObject( ++lineNumber ) );
                continue;
            }
            ArrayList<String> entries = readCompleteHSSFRow(row);
            
            if ( start ) {
                actualFileHeader = actualHeader( entries, fileHeaderMapper );
                if ( actualFileHeader == null ) {
                    LOG.debug( "mandatory feilds don't exist for file : {}", xlsFileURI );
                    //Find the headers which weren't matched with the system
                    intersectionHeaders( entries, fileHeaderMapper.values() );
                    //return null to differentiate if it failed because of headers
                    return null;
                }
                ++lineNumber;
                start = false;
            } else if(ifEntryNotEmpty(entries)) {

                if ( actualFileHeader.containsKey( "fundedDate" ) ) {
                    String actualFileDate = entries.get( actualFileHeader.get( "fundedDate" ) );

                    if ( dateFormat == null ) {
                        dateFormat = multipleDateFormat( DATE_FORMATS, actualFileDate );
                    }

                    Date fileDate = new SimpleDateFormat( dateFormat ).parse( actualFileDate );
                    systemDate = new SimpleDateFormat( SYSTEM_DATE_FORMAT ).format( fileDate );


                } else
                    systemDate = new SimpleDateFormat( SYSTEM_DATE_FORMAT ).format( Calendar.getInstance().getTime() );


                surveyList.add( creatingSurveyObject( entries, actualFileHeader, systemDate, ++lineNumber ) );

            }else {
                surveyList.add(creatingEmptySurveyObject(++lineNumber));
            }
           
        }
        return surveyList;
    }
    
    private ArrayList<String> readCompleteHSSFRow( HSSFRow row )
    {
        ArrayList<String> entries = new ArrayList<>();
        for ( int i = 0; i < row.getLastCellNum(); i++ ) {
            Cell cell = row.getCell( i );
            if ( cell == null ) {
                entries.add( "" );
            } else {
                entries.add( new DataFormatter().formatCellValue( cell ) );
            }
        }
        return entries;
    }

    private SurveyPutVO creatingSurveyObject(ArrayList<String> entries, Map<String, Integer> actualFileHeader, String systemDate, int lineNumber ) {
        SurveyPutVO surveyVO = new SurveyPutVO();
        TransactionInfoPutVO transactionInfoPutVO = new TransactionInfoPutVO();
        ServiceProviderInfo serviceProviderInfo = new ServiceProviderInfo();
        
        
        transactionInfoPutVO.setCustomer1Email( entries.get(actualFileHeader.get( "bor1Email" )).trim() );
        transactionInfoPutVO.setCustomer1FirstName( entries.get(actualFileHeader.get( "bor1FirstName" )) );
        transactionInfoPutVO.setCustomer1LastName(actualFileHeader.containsKey( "bor1LastName" ) ? entries.get(actualFileHeader.get( "bor1LastName" )) : "" );
        transactionInfoPutVO.setCustomer2Email( actualFileHeader.containsKey( "bor2Email" ) ? entries.get(actualFileHeader.get( "bor2Email" )).trim() : "" );
        transactionInfoPutVO.setCustomer2FirstName( actualFileHeader.containsKey( "bor2FirstName" ) ?  entries.get(actualFileHeader.get( "bor2FirstName" )) : "" );
        transactionInfoPutVO.setCustomer2LastName(actualFileHeader.containsKey( "bor2LastName" ) ?  entries.get(actualFileHeader.get( "bor2LastName" )) : "");
        transactionInfoPutVO.setTransactionCity(actualFileHeader.containsKey( "subPropCity" ) ? entries.get(actualFileHeader.get( "subPropCity" )) : ""  );
        transactionInfoPutVO.setTransactionDate(systemDate);
        transactionInfoPutVO.setTransactionRef( actualFileHeader.containsKey( "fileName" ) ? entries.get(actualFileHeader.get( "fileName" )) : "" );
        transactionInfoPutVO.setTransactionState(actualFileHeader.containsKey( "subPropState" ) ?  entries.get(actualFileHeader.get( "subPropState" ))  : "" );
        transactionInfoPutVO.setTransactionType(actualFileHeader.containsKey( "loanPurpose" ) ?  entries.get(actualFileHeader.get( "loanPurpose" ))  : "" );
        transactionInfoPutVO.setTransactionType(actualFileHeader.containsKey( "propertyAddress" ) ?  entries.get(actualFileHeader.get( "propertyAddress" ))  : "" );
        //adding buyer and seller feilds
        transactionInfoPutVO.setBuyerAgentEmail( actualFileHeader.containsKey( "buyerAgentEmail" ) ? entries.get(actualFileHeader.get( "buyerAgentEmail" )).trim() : "" );
        transactionInfoPutVO.setBuyerAgentFirstName( actualFileHeader.containsKey( "buyerAgentFirstName" ) ?  entries.get(actualFileHeader.get( "buyerAgentFirstName" )) : "" );
        transactionInfoPutVO.setBuyerAgentLastName(actualFileHeader.containsKey( "buyerAgentLastName" ) ?  entries.get(actualFileHeader.get( "buyerAgentLastName" )) : "");
        transactionInfoPutVO.setSellerAgentEmail( actualFileHeader.containsKey( "sellerAgentEmail" ) ? entries.get(actualFileHeader.get( "sellerAgentEmail" )).trim() : "" );
        transactionInfoPutVO.setSellerAgentFirstName( actualFileHeader.containsKey( "sellerAgentFirstName" ) ?  entries.get(actualFileHeader.get( "sellerAgentFirstName" )) : "" );
        transactionInfoPutVO.setSellerAgentLastName(actualFileHeader.containsKey( "sellerAgentLastName" ) ?  entries.get(actualFileHeader.get( "sellerAgentLastName" )) : "");
        //adding buyer and seller participants 
    
        serviceProviderInfo.setServiceProviderEmail( entries.get(actualFileHeader.get( "serviceremail" )).trim()  );
        serviceProviderInfo.setServiceProviderName(  entries.get(actualFileHeader.get( "servicer" ))   );
        
        
        //adding line count
        surveyVO.setLineNumber( lineNumber );
        surveyVO.setServiceProviderInfo( serviceProviderInfo );
        surveyVO.setTransactionInfo( transactionInfoPutVO );
        return surveyVO;
    }

    private TransactionSourceFtp fetchFtpData( long companyId, long ftpId )
    {
        Call<TransactionSourceFtp> requestCall = RetrofitApiBuilder.apiBuilderInstance().getSSAPIIntergrationService()
            .getFtpCrm( companyId, ftpId );
        try {
            Response<TransactionSourceFtp> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "response {}", response.body() );
            }
            return response.body();
        } catch ( IOException e ) {
            LOG.error( "IOException/ APIIntergrationException caught", e );
            return null;
        }

    }
    
    private Map<String,Integer> actualHeader(ArrayList<String> entries , Map<String,String> fileHeaderMapper){
        Map<String,Integer> internalFileHeader = new HashMap<>();
        Map<String,Integer> actualFileHeader = new HashMap<>();
        int position = 0;
        boolean allMandatoryFeildsExist = true;

        for ( String headerEntry : entries ) {
            internalFileHeader.put( headerEntry.trim(), position++ );
        }
        for(Entry<String, String> fileEntry : fileHeaderMapper.entrySet()) {
            if(internalFileHeader.get(  fileEntry.getValue().trim() ) != null)
                actualFileHeader.put( fileEntry.getKey(), internalFileHeader.get(  fileEntry.getValue().trim() ) );
        }
        
        for(String mandatory : MANDATORY_FIELDS) {
            if(allMandatoryFeildsExist) {
                allMandatoryFeildsExist = actualFileHeader.get( mandatory )== null ? false : true;
            }else {
                LOG.debug( "mandatory feilds don't exist for file " );
                //call another api if all madatory feilds don't exist 
                //and break
                return null;
            }
        }
        return actualFileHeader;
    }


    private BulkSurveyPutVO convertToBulk( List<SurveyPutVO> surveyList, long companyId, String source )
    {
        BulkSurveyPutVO bulkSurveyPutVO = new BulkSurveyPutVO();
        bulkSurveyPutVO.setSurveys( surveyList );
        bulkSurveyPutVO.setCompanyId( companyId );
        bulkSurveyPutVO.setSource( source );
        return bulkSurveyPutVO;
    }
    
    //this is to remove the BOM character if exists in csv 
    //disrupts the header mapping cause it's usually present in the first line 
    private static String removeUTF8BOM(String firstLine) {
        if (firstLine.startsWith(UTF8_BOM)) {
            firstLine = firstLine.substring(1);
        }
        return firstLine;
    }
    
    //multiple date formats
    private String multipleDateFormat(List<String> dateFormats , String strDate) {
        for(String format: dateFormats){
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            try{
                sdf.parse(strDate);
                return format;
            } catch (ParseException e) {
                 LOG.debug( "The date format : {} , did not parse for date : {}",format,strDate );
            }
        }
            return "";
    }
    

    //finding the header's from file which weren't mapped to any header in the mapping
    //basically an intersection of the functionality
    private void intersectionHeaders( ArrayList<String> internalFileKey, Collection<String> fileHeaderValues )
    {
        UNMATCHED_FILE_HEADERS = new ArrayList<>();
        for ( String fileHeader : internalFileKey ) {
            if ( !fileHeaderValues.contains( fileHeader ) && !fileHeader.isEmpty()) {
                UNMATCHED_FILE_HEADERS.add( fileHeader );
            }
        }
    }


    //error message for no mandatory headers
    private String createErrorForHeaders()
    {
        String createErrorMessage = "The system required mandatory header's <i>" + Arrays.toString( MANDATORY_FIELDS )
            + "</i> were not mapped <br/> The file headers which don't match the system's mapping are <br/> <ul>";
        for ( String unmatched : UNMATCHED_FILE_HEADERS )
            createErrorMessage += "<li>" + unmatched + "</li>";
        createErrorMessage += "</ul>";
        return createErrorMessage;
    }
    
    private List<SurveyPutVO> createSurveyList(Path targetPath, Map<String,String> fileHeaderMapper, TransactionIngestionMessage transactionIngestionMessage) throws IOException {
        List<SurveyPutVO> surveyList = new ArrayList<>();
        String extension = getExtention( targetPath );
        try {
            if ( extension.equals( "csv" ) ) {
                surveyList = readFromCsv( targetPath.toString(), fileHeaderMapper );
            } else if ( extension.equals( "xlsx" ) ) {
                surveyList = readFromXlsx( targetPath.toString(), fileHeaderMapper );
            } else if ( extension.equals( "xls" ) ) {
                surveyList = readFromXls( targetPath.toString(), fileHeaderMapper );
            }
        } catch ( ParseException e ) {
            LOG.warn( "System is not able to parse the given date" );
            SSAPIOperations.getInstance().processFailedFtpRequest( "System is not able to parse the given date in the file",
                transactionIngestionMessage, false );
        }
        return surveyList;
    }
    
    //check if the complete row is empty
    private boolean ifEntryNotEmpty(ArrayList<String> entries) {
        boolean isNotEmpty = false;
        for(String entry : entries){
            if(!entry.trim().isEmpty()) {
                isNotEmpty = true;
                break;
            }
        }
        return isNotEmpty;
    }

    //creating null ServiceProviderInfo and TransactionInfo object if the row is empty
    private SurveyPutVO creatingEmptySurveyObject(int lineNumber ) {
        SurveyPutVO surveyVO = new SurveyPutVO();
        //adding line count
        surveyVO.setLineNumber( lineNumber );
        surveyVO.setServiceProviderInfo( null );
        surveyVO.setTransactionInfo( null );
        return surveyVO;
    }
}
