package com.realtech.socialsurvey.compute.entities.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class InstagramResponse implements Serializable {

    @SerializedName("connected_instagram_account")
    private ConnectedInstagramAccount connectedInstagramAccount;

    private String id;

    public ConnectedInstagramAccount getConnectedInstagramAccount() {
        return this.connectedInstagramAccount;
    }

    public void setConnectedInstagramAccount(ConnectedInstagramAccount connectedInstagramAccount) {
        this.connectedInstagramAccount = connectedInstagramAccount;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "InstagramResponse{" +
                "connectedInstagramAccount=" + connectedInstagramAccount +
                ", id='" + id + '\'' +
                '}';
    }
}
