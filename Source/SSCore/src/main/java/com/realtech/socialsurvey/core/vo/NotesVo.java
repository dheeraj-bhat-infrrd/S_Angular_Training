package com.realtech.socialsurvey.core.vo;

import com.realtech.socialsurvey.core.entities.Notes;

import java.io.Serializable;


public class NotesVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private long companyId;
    private String note;
    private String createdBy;

    public NotesVo()
    {
    }

    public long getCompanyId()
    {
        return companyId;
    }


    public void setCompanyId( long companyId )
    {
        this.companyId = companyId;
    }


    public String getNote()
    {
        return note;
    }


    public void setNote( String note )
    {
        this.note = note;
    }


    public String getCreatedBy()
    {
        return createdBy;
    }


    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
    }


    @Override public String toString()
    {
        return "NotesVo{" + "companyId=" + companyId + ", note='" + note + '\'' + ", createdBy=" + createdBy + '}';
    }


    /**
     * Utility for converting VO to entity
     * @param notesVo
     * @return
     */
    public static Notes convertToNotesEntity(NotesVo notesVo){
        Notes notes = new Notes(  );
        notes.setNote( notesVo.getNote() );
        notes.setCreatedBy( notesVo.getCreatedBy() );
        notes.setCreatedOn( System.currentTimeMillis() );
        return notes;
    }
}
