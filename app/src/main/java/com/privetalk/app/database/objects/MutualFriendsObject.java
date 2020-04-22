package com.privetalk.app.database.objects;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by zachariashad on 07/03/16.
 */
public class MutualFriendsObject implements Serializable {
    public String name;
    public String fbID;

    public MutualFriendsObject(JSONObject obj) {
        this.name = obj.optString("name");
        this.fbID = obj.optString("id");
    }
}
