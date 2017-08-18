package com.realtech.socialsurvey.core.entities;

import com.realtech.socialsurvey.core.enums.HierarchyType;


public class HierarchyRelocationTarget
{
    private Company targetCompany;
    private Region targetRegion;
    private Branch targetBranch;
    private HierarchyType hierarchyType;
    
    private int surveyRelcation;


    public int getSurveyRelcation()
    {
        return surveyRelcation;
    }


    public void setSurveyRelcation( int surveyRelcation )
    {
        this.surveyRelcation = surveyRelcation;
    }


    public Company getTargetCompany()
    {
        return targetCompany;
    }


    public void setTargetCompany( Company targetCompany )
    {
        this.targetCompany = targetCompany;
    }


    public Region getTargetRegion()
    {
        return targetRegion;
    }


    public void setTargetRegion( Region targetRegion )
    {
        this.targetRegion = targetRegion;
    }


    public Branch getTargetBranch()
    {
        return targetBranch;
    }


    public void setTargetBranch( Branch targetBranch )
    {
        this.targetBranch = targetBranch;
    }


    public HierarchyType getHierarchyType()
    {
        return hierarchyType;
    }


    public void setHierarchyType( HierarchyType hierarchyType )
    {
        this.hierarchyType = hierarchyType;
    }

}
