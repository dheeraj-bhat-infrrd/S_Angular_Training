package com.realtech.socialsurvey.core.entities;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;


public class JobLogDetailsResponse
{


    boolean successful;
    boolean failure;
    boolean inProgress;
    JobLogDetailsTimeAndStatus failureStatus;
    JobLogDetailsTimeAndStatus progressStatus;
    JobLogDetailsTimeAndStatus lastRunTime;


    public boolean isSuccessful()
    {
        return successful;
    }


    public void setSuccessful( boolean successful )
    {
        this.successful = successful;
    }


    public boolean isFailure()
    {
        return failure;
    }


    public void setFailure( boolean failure )
    {
        this.failure = failure;
    }


    public boolean isInProgress()
    {
        return inProgress;
    }


    public void setInProgress( boolean inProgress )
    {
        this.inProgress = inProgress;
    }


    public JobLogDetailsTimeAndStatus getFailureStatus()
    {
        return failureStatus;
    }


    public void setFailureStatus( JobLogDetailsTimeAndStatus failureStatus )
    {
        this.failureStatus = failureStatus;
    }


    public JobLogDetailsTimeAndStatus getProgressStatus()
    {
        return progressStatus;
    }


    public void setProgressStatus( JobLogDetailsTimeAndStatus progressStatus )
    {
        this.progressStatus = progressStatus;
    }


    public JobLogDetailsTimeAndStatus getLastRunTime()
    {
        return lastRunTime;
    }


    public void setLastRunTime( JobLogDetailsTimeAndStatus lastRunTime )
    {
        this.lastRunTime = lastRunTime;
    }


    @Override
    public String toString()
    {
        return "JobLogDetailsResponse [successful=" + successful + ", failure=" + failure + ", inProgress=" + inProgress
            + ", failureStatus=" + failureStatus + ", progressStatus=" + progressStatus + ", lastRunTime=" + lastRunTime + "]";
    }


    public class JobLogDetailsTimeAndStatus
    {

        String est;
        String pst;
        String ist;
        Timestamp timestamp;
        String currentJob;
        String status;


        public String convertDateTimeZone( Timestamp date, String toTimeZone )
        {
            Calendar calendar = Calendar.getInstance( TimeZone.getTimeZone( toTimeZone ) );
            SimpleDateFormat dateFormat = new SimpleDateFormat( "MM/dd/yy, HH:mm" );
            dateFormat.setCalendar( calendar );
            String dateString = dateFormat.format( date );
            return dateString;
        }


        public String getEst()
        {
            return est;
        }


        public void setEst()
        {

            this.est = convertDateTimeZone( timestamp, "EST" );

        }


        public String getPst()
        {
            return pst;
        }


        public void setPst()
        {
            this.pst = convertDateTimeZone( timestamp, "PDT" );
        }


        public String getIst()
        {
            return ist;
        }


        public void setIst()
        {
            this.ist = convertDateTimeZone( timestamp, "Asia/Kolkata" );
        }


        public Timestamp getTimestamp()
        {
            return timestamp;
        }


        public void setTimestamp( Timestamp timestamp )
        {
            this.timestamp = timestamp;
        }


        public String getCurrentJob()
        {
            return currentJob;
        }


        public void setCurrentJob( String currentJob )
        {
            this.currentJob = currentJob;
        }


        public String getStatus()
        {
            return status;
        }


        public void setStatus( String status )
        {
            this.status = status;
        }


        @Override
        public String toString()
        {
            return "JobTimeAndStatus [est=" + est + ", pst=" + pst + ", ist=" + ist + ", timestamp=" + timestamp
                + ", currentJob=" + currentJob + ", status=" + status + "]";
        }
    }
}

