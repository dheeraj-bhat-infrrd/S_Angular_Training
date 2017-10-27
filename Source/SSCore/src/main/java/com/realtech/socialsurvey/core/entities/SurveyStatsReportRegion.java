package com.realtech.socialsurvey.core.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="survey_stats_report_region")
public class SurveyStatsReportRegion
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    @Column ( name = "survey_stats_report_id")
    private String surveyStatsReportId;
    
    @Column( name = "Id")
    private String id;
    
    @Column( name = "company_id")
    private long companyId;
    
    @Column( name = "region_id")
    private long regionId;
    
    @Column( name = "trx_month")
    private String trxMonth;
    
    @Column( name = "trx_rcvd")
    private long trxRcvd;
    
    @Column( name = "pending")
    private long pending;
    
    @Column( name = "duplicates")
    private long duplicates;
    
    @Column( name = "corrupted")
    private long corrupted;
    
    @Column( name = "abusive")
    private long abusive;
    
    @Column( name = "old_records")
    private long oldRecords;
    
    @Column( name = "ignored")
    private long ignored;
    
    @Column( name = "mismatched")
    private long mismatched;
    
    @Column( name = "sent_count ")
    private long sentCount;
    
    @Column( name = "clicked_count")
    private long clickedCount;
    
    @Column( name = "completed")
    private long completed;
    
    @Column( name = "partially_completed")
    private long partiallyCompleted;
    
    @Column( name = "complete_percentage")
    private double completePercentage;
    
    @Column( name = "delta")
    private long delta;
    
    @Column( name = "created_date")
    private Date created_date;
    
    @Column( name = "year") 
    private long year;
    
    @Column( name = "month") 
    private long month;
    
    @Column( name = "avg_rating")
    private float avgRating;
    
    @Column( name = "detractors")
    private long detractors;
    
    @Column( name = "passives")
    private long passives;
    
    @Column( name = "promoters") 
    private long promoters;
    
    @Column( name = "region_name") 
    private String regionName;
    
    @Column( name = "company_name")
    private String companyName;

    @Column( name = "incomplete")
    private long incomplete;
    public String getSurveyStatsReportId()
    {
        return surveyStatsReportId;
    }

    public void setSurveyStatsReportId( String surveyStatsReportId )
    {
        this.surveyStatsReportId = surveyStatsReportId;
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public long getCompanyId()
    {
        return companyId;
    }

    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }

    public long getRegionId()
    {
        return regionId;
    }

    public void setRegionId( long regionId )
    {
        this.regionId = regionId;
    }

    public String getTrxMonth()
    {
        return trxMonth;
    }

    public void setTrxMonth( String trxMonth )
    {
        this.trxMonth = trxMonth;
    }

    public long getTrxRcvd()
    {
        return trxRcvd;
    }

    public void setTrxRcvd( long trxRcvd )
    {
        this.trxRcvd = trxRcvd;
    }

    public long getPending()
    {
        return pending;
    }

    public void setPending( long pending )
    {
        this.pending = pending;
    }

    public long getDuplicates()
    {
        return duplicates;
    }

    public void setDuplicates( long duplicates )
    {
        this.duplicates = duplicates;
    }

    public long getCorrupted()
    {
        return corrupted;
    }

    public void setCorrupted( long corrupted )
    {
        this.corrupted = corrupted;
    }

    public long getAbusive()
    {
        return abusive;
    }

    public void setAbusive( long abusive )
    {
        this.abusive = abusive;
    }

    public long getOldRecords()
    {
        return oldRecords;
    }

    public void setOldRecords( long oldRecords )
    {
        this.oldRecords = oldRecords;
    }

    public long getIgnored()
    {
        return ignored;
    }

    public void setIgnored( long ignored )
    {
        this.ignored = ignored;
    }

    public long getMismatched()
    {
        return mismatched;
    }

    public void setMismatched( long mismatched )
    {
        this.mismatched = mismatched;
    }

    public long getSentCount()
    {
        return sentCount;
    }

    public void setSentCount( long sentCount )
    {
        this.sentCount = sentCount;
    }

    public long getClickedCount()
    {
        return clickedCount;
    }

    public void setClickedCount( long clickedCount )
    {
        this.clickedCount = clickedCount;
    }

    public long getCompleted()
    {
        return completed;
    }

    public void setCompleted( long completed )
    {
        this.completed = completed;
    }

    public long getPartiallyCompleted()
    {
        return partiallyCompleted;
    }

    public void setPartiallyCompleted( long partiallyCompleted )
    {
        this.partiallyCompleted = partiallyCompleted;
    }

    public double getCompletePercentage()
    {
        return completePercentage;
    }

    public void setCompletePercentage( double completePercentage )
    {
        this.completePercentage = completePercentage;
    }

    public long getDelta()
    {
        return delta;
    }

    public void setDelta( long delta )
    {
        this.delta = delta;
    }

    public Date getCreated_date()
    {
        return created_date;
    }

    public void setCreated_date( Date created_date )
    {
        this.created_date = created_date;
    }

    public long getYear()
    {
        return year;
    }

    public void setYear( long year )
    {
        this.year = year;
    }

    public long getMonth()
    {
        return month;
    }

    public void setMonth( long month )
    {
        this.month = month;
    }

    public float getAvgRating()
    {
        return avgRating;
    }

    public void setAvgRating( float avgRating )
    {
        this.avgRating = avgRating;
    }

    public long getDetractors()
    {
        return detractors;
    }

    public void setDetractors( long detractors )
    {
        this.detractors = detractors;
    }

    public long getPassives()
    {
        return passives;
    }

    public void setPassives( long passives )
    {
        this.passives = passives;
    }

    public long getPromoters()
    {
        return promoters;
    }

    public void setPromoters( long promoters )
    {
        this.promoters = promoters;
    }

    public String getRegionName()
    {
        return regionName;
    }

    public void setRegionName( String regionName )
    {
        this.regionName = regionName;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setCompanyName( String companyName )
    {
        this.companyName = companyName;
    }

    public long getIncomplete()
    {
        return incomplete;
    }

    public void setIncomplete( long incomplete )
    {
        this.incomplete = incomplete;
    }

    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        return "SurveyStatsReportBranch [surveyStatsReportId=" + surveyStatsReportId + ", id=" + id + ", companyId=" + companyId + ", regionId=" + regionId
                + ", trxMonth=" + trxMonth + ", trxRcvd=" + trxRcvd + ", pending=" + pending + ", duplicates=" + duplicates + ", corrupted=" +  corrupted + ", abusive=" + abusive +", "
                + "oldRecords=" + oldRecords + ", ignored=" + ignored + ", mismatched=" + mismatched + ", sentCount=" +
                sentCount + ", clickedCount=" +clickedCount + ", completed=" +completed + ", partiallyCompleted=" +partiallyCompleted + ", completePercentage=" +
                completePercentage +", delta=" +delta +", created_date=" +created_date +", year=" +year +", month=" +month+", avgRating=" +avgRating+", detractors=" +detractors+", passives=" +passives+
                ", promoters=" +promoters+", regionName=" +regionName+", companyName=" +companyName+", incomplete=" +incomplete+"]";
    }
    

}
