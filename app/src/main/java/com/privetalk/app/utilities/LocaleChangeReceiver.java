package com.privetalk.app.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.database.datasource.AttributesDatasource;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.datasource.ETagDatasource;
import com.privetalk.app.database.datasource.InterestsDatasource;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by emilios on 13/02/17.
 */

public class LocaleChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("locationchanged", String.valueOf(Locale.getDefault()));

        String locale = Locale.getDefault().toString();

        if (locale.contains("el") || locale.contains("ru") || locale.contains("en")) {

            InterestsDatasource.getInstance(context).deleteAllInterests();
            ETagDatasource.getInstance(context).deleteEtags();
            AttributesDatasource.getInstance(context).deleteAll();

            JSONObject postParam = new JSONObject();

            try {
                postParam.put("language_code", Locale.getDefault());
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JsonObjectRequest postLocation = new JsonObjectRequest(Request.Method.POST, Links.SET_PROFILE_SETTINGS, postParam,

                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {


                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    if (error.networkResponse != null) {
                        try {
                            Log.d("locationchangedError ", new String(error.networkResponse.data, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            })

            {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

                    Log.d("locationchanged response", String.valueOf(response.statusCode));

                    return super.parseNetworkResponse(response);
                }

                /**
                 * Passing some request headers
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                    headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

                    return headers;
                }

            };

            VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(postLocation);

        }

    }
}