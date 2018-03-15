package com.realtech.socialsurvey.compute.entities.response;

import java.util.ArrayList;

public class InstagramMedia {

    private static final long serialVersionUID = 1L;

    private ArrayList<InstagramMediaData> data;

    private FBPaging paging;

    public ArrayList<InstagramMediaData> getData() { return this.data; }

    public void setData(ArrayList<InstagramMediaData> data) { this.data = data; }

    public FBPaging getPaging() { return this.paging; }

    public void setPaging(FBPaging paging) { this.paging = paging; }
}
