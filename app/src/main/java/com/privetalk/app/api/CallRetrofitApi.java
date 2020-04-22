package com.privetalk.app.api;


import com.privetalk.app.utilities.Constants;

import java.util.HashMap;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;


public interface CallRetrofitApi {

    /*  obj.put(Constants.CLIENT_ID,getResources().getString(R.string.instagram_client_id));
  obj.put(Constants.CODE,code);
  obj.put(Constants.CLIENT_SECRET,getResources().getString(R.string.instagram_client_secret));
  obj.put(Constants.GRANT_TYPE,getResources().getString(R.string.instagram_grant_type));
  obj.put(Constants.REDIRECT_URL,getResources().getString(R.string.instagram_redirect_url));*/
    @Multipart
    @POST("access_token")
    Call<GetAccessTokenResponse> getAccessToken(@PartMap HashMap<String, RequestBody> params);

}
