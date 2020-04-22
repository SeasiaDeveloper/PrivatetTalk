package com.privetalk.app.api;

public class GetAccessTokenResponse {
    private String access_token;
    private String user_id;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /*  {
        "access_token": "IGQVJWSWt6YTJMQTJvVE5FclRGdlZATdkp0YjY2aWs5aWZAmbXRRRlhwUGhkTGtSMHdMWHduakdoSDJJTnBweno5SDNuNnp1SGlINVR4bGtTb2p4QktsUG5xM0I3a0xYYzdTdF9tUUtWVFp1QVY5UnFoc2pWbUxqMEx2U0RR",
            "user_id": 17841407596483246
    }*/
}
