package com.realtech.socialsurvey.core.vo;

import java.util.List;

import com.realtech.socialsurvey.core.entities.User;

public class UserList
{

    private long totalRecord;
    private  List<User> users;
    
    
    public long getTotalRecord()
    {
        return totalRecord;
    }
    public void setTotalRecord( long totalRecord )
    {
        this.totalRecord = totalRecord;
    }
    public List<User> getUsers()
    {
        return users;
    }
    public void setUsers( List<User> users )
    {
        this.users = users;
    }
}
