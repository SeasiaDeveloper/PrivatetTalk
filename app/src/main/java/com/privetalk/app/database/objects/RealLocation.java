package com.privetalk.app.database.objects;

import com.privetalk.app.utilities.PriveTalkConstants;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;

/**
 * Created by zachariashad on 25/02/16.
 */
public class RealLocation implements Serializable {


    public RealLocation() {
    }

    public RealLocation(JSONObject obj) {
        JSONObject lastLocObj = obj.optJSONObject("last_location");

        if (lastLocObj == null)
            return;
        this.latitude = lastLocObj.optString("latitude");
        this.longitute = lastLocObj.optString("longitude");

        try {
            this.lastUpdate = PriveTalkConstants.PROMOTION_DATE.parse(obj.optString("last_update")).getTime();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        this.isoCountryCode = obj.optString("iso_country_code");
        this.postalCode = obj.optString("postal_code");
        this.administrativeArea = obj.optString("administrative_area");

    }

    public String longitute;
    public String latitude;
    public long lastUpdate;
    public String isoCountryCode;
    public String postalCode;
    public String administrativeArea;
}
