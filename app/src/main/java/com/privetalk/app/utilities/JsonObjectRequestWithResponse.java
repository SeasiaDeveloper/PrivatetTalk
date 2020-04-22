package com.privetalk.app.utilities;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.database.datasource.ETagDatasource;
import com.privetalk.app.database.objects.EtagObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by zeniosagapiou on 25/02/16.
 */
public class JsonObjectRequestWithResponse extends JsonObjectRequest {

    public JsonObjectRequestWithResponse(int method, String url, String requestBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
    }

    public JsonObjectRequestWithResponse(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    public JsonObjectRequestWithResponse(int method, String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public JsonObjectRequestWithResponse(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public JsonObjectRequestWithResponse(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

        ETagDatasource.getInstance(PriveTalkApplication.getInstance()).saveETagForLink(new EtagObject(getUrl(), response));
        try {

            String data = "";
            JSONObject jsonObject = new JSONObject();

            if (response.statusCode != 304) {
                data = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                 jsonObject = new JSONObject(data);
            }

            JSONObject newObject = new JSONObject();

            newObject.put("data", jsonObject);
            newObject.put("statusCode", response.statusCode);

            return Response.success(newObject,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }

    }
}
