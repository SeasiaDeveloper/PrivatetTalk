package com.privetalk.app.database.objects;

import android.database.Cursor;

import com.android.volley.NetworkResponse;

import com.privetalk.app.database.PriveTalkTables;

/**
 * Created by zeniosagapiou on 18/02/16.
 */
public class EtagObject {

    public String link;
    public String etag;

    public EtagObject(Cursor cursor) {
        this.link = cursor.getString(cursor.getColumnIndex(PriveTalkTables.ETagTable.LINK));
        this.etag = cursor.getString(cursor.getColumnIndex(PriveTalkTables.ETagTable.ETAG));
    }

    public EtagObject(String link, NetworkResponse networkResponse) {
        this.link = link;
        if (networkResponse != null)
            this.etag = networkResponse.headers.get("ETag");
        else
            this.etag = "";
    }
}
