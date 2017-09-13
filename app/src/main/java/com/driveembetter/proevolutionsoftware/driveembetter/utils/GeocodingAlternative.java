package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by alfredo on 13/09/17.
 */

public class GeocodingAlternative {

    private final static String TAG = GeocodingAlternative.class.getSimpleName();

    // Resources
    private final static JSONParser jsonParser = new JSONParser();
    private final static String httpResource = "http://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=true";
    private String Address1 = "";
    private String Address2 = "";
    private String City = "";
    private String State = "";
    private String Country = "";
    private String County = "";
    private String PIN = "";



    public void getAddress() {
        try {
            Object obj= GeocodingAlternative.jsonParser;
            JSONArray array=(JSONArray)obj;

            // JSONObject jsonObj = GeocodingAlternative.getJSONfromURL(GeocodingAlternative.httpResource);
            JSONObject jsonObj = null;
            String Status = jsonObj.getString("status");
            if (Status.equalsIgnoreCase("OK")) {
                JSONArray Results = jsonObj.getJSONArray("results");
                JSONObject zero = Results.getJSONObject(0);
                JSONArray address_components = zero.getJSONArray("address_components");

                for (int i = 0; i < address_components.length(); i++) {
                    JSONObject zero2 = address_components.getJSONObject(i);
                    String long_name = zero2.getString("long_name");
                    JSONArray mtypes = zero2.getJSONArray("types");
                    String Type = mtypes.getString(0);

                    if (long_name != null && !TextUtils.isEmpty(long_name) && long_name.length() > 0) {
                        if (Type.equalsIgnoreCase("street_number")) {
                            Address1 = long_name + " ";
                        } else if (Type.equalsIgnoreCase("route")) {
                            Address1 = Address1 + long_name;
                        } else if (Type.equalsIgnoreCase("sublocality")) {
                            Address2 = long_name;
                        } else if (Type.equalsIgnoreCase("locality")) {
                            // Address2 = Address2 + long_name + ", ";
                            City = long_name;
                        } else if (Type.equalsIgnoreCase("administrative_area_level_2")) {
                            County = long_name;
                        } else if (Type.equalsIgnoreCase("administrative_area_level_1")) {
                            State = long_name;
                        } else if (Type.equalsIgnoreCase("country")) {
                            Country = long_name;
                        } else if (Type.equalsIgnoreCase("postal_code")) {
                            PIN = long_name;
                        }
                    }

                    // JSONArray mtypes = zero2.getJSONArray("types");
                    // String Type = mtypes.getString(0);
                    // Log.e(Type,long_name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAddress1() {
        return this.Address1;
    }

    public String getAddress2() {
        return this.Address2;
    }

    public String getCity() {
        return this.City;
    }

    public String getState() {
        return this.State;
    }

    public String getCountry() {
        return this.Country;
    }

    public String getCounty() {
        return County;
    }

    public String getPIN() {
        return this.PIN;
    }
}