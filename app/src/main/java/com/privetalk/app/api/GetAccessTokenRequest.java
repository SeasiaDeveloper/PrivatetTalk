package com.privetalk.app.api;

public class GetAccessTokenRequest {
    private String client_id;
    private String client_secret;
    private String grant_type;
    private String redirect_uri;
    private String code;

    public GetAccessTokenRequest(String client_id, String client_secret, String grant_type, String redirect_uri, String code) {
        this.client_id = client_id;
        this.client_secret = client_secret;
        this.grant_type = grant_type;
        this.redirect_uri = redirect_uri;
        this.code = code;
    }

}
