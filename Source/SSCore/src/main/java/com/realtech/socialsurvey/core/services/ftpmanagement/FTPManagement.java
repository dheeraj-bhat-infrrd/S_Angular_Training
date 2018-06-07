package com.realtech.socialsurvey.core.services.ftpmanagement;

import java.util.List;

import com.realtech.socialsurvey.core.entities.TransactionSourceFtp;
import com.realtech.socialsurvey.core.entities.ftp.FtpUploadRequest;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;


public interface FTPManagement
{

    public void startFTPFileProcessing();


    public List<TransactionSourceFtp> getFtpConnections( String status, int startIndex, int batchSize,
        boolean doHideSensitiveInfo );


    public String processFailedStormMessage( FtpUploadRequest failedFtpUpload, String errorMessage, boolean sendOnlyToSocialSurveyAdmin )
        throws InvalidInputException, UndeliveredEmailException;


    public boolean updateRetryFailedForFailedFtpRequest( String id ) throws InvalidInputException;

}
