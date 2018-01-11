package com.realtech.socialsurvey.compute.entities;

public class EmailAttachment
{
    private String fileName;
    private String filePath;
    
    public EmailAttachment(){
        super();
    }
    
    public EmailAttachment(String fileName, String filePath){
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getFileName()
    {
        return fileName;
    }
    
    public void setFileName( String filename )
    {
        this.fileName = filename;
    }
    
    public String getFilePath()
    {
        return filePath;
    }
    
    public void setFilePath( String filepath )
    {
        this.filePath = filepath;
    }

    @Override
    public String toString()
    {
        return "EmailAttachment [fileName=" + fileName + ", filePath=" + filePath + "]";
    }
}