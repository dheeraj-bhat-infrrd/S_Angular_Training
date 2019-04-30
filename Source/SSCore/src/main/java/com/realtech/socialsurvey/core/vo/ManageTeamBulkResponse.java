package com.realtech.socialsurvey.core.vo;

import java.io.Serializable;
import java.util.List;


public class ManageTeamBulkResponse implements Serializable
{
    private static final long serialVersionUID = 1L;

    private List<ManageTeamBulkActionVo> successItems;
    private List<ManageTeamBulkActionVo> failedItems;


    public ManageTeamBulkResponse()
    {
    }


    public ManageTeamBulkResponse( List<ManageTeamBulkActionVo> successItems, List<ManageTeamBulkActionVo> failedItems )
    {
        this.successItems = successItems;
        this.failedItems = failedItems;
    }


    public List<ManageTeamBulkActionVo> getSuccessItems()
    {
        return successItems;
    }


    public void setSuccessItems( List<ManageTeamBulkActionVo> successItems )
    {
        this.successItems = successItems;
    }


    public List<ManageTeamBulkActionVo> getFailedItems()
    {
        return failedItems;
    }


    public void setFailedItems( List<ManageTeamBulkActionVo> failedItems )
    {
        this.failedItems = failedItems;
    }

}
