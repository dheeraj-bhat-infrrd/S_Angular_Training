package com.realtech.socialsurvey.compute.utils;

import com.realtech.socialsurvey.compute.common.LocalPropertyFileHandler;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.realtech.socialsurvey.compute.common.ComputeConstants.APPLICATION_PROPERTY_FILE;
import static com.realtech.socialsurvey.compute.common.ComputeConstants.FILEUPLOAD_DIRECTORY_LOCATION;


public class FileUtils
{
    private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

    public static File createFileInLocal( String fileName, XSSFWorkbook workbook ) throws IOException
    {

        // create excel file
        LOG.info("creating excel file on local system ");
        FileOutputStream fileOutput = null;
        File file = null;
        String fileDirectoryLocation = LocalPropertyFileHandler.getInstance()
            .getProperty(APPLICATION_PROPERTY_FILE, FILEUPLOAD_DIRECTORY_LOCATION).orElse(null);
        LOG.info("File Location : {}", fileDirectoryLocation);
        try {
            file = new File(fileDirectoryLocation + File.separator + fileName);
            // write output to the file
            if (file.createNewFile()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("File created at {}. File Name {}", file.getAbsolutePath(), fileName);
                }
                fileOutput = new FileOutputStream(file);
                LOG.debug("Created file output stream to write into {}", fileName);
                workbook.write(fileOutput);
                LOG.debug("Wrote into file {}", fileName);
            }
            LOG.debug("Excel creation status {}", file.exists());
        }  finally {
            try {
                if (fileOutput != null)
                    fileOutput.close();
            } catch (IOException e) {
                LOG.error("Exception caught while generating report " + fileName + ": " + e.getMessage());
            }
        }
        return file;
    }
}
