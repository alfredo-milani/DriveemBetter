package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONValue;

/**
 * Created by alfredo on 13/09/17.
 */

public class JSONParser {

    private final static String TAG = JSONParser.class.getSimpleName();

    public static void getJSONfromURL() {
        String s="[0,{\"1\":{\"2\":{\"3\":{\"4\":[5,{\"6\":7}]}}}}]"; Object obj=JSONValue.parse(s); JSONArray array=(JSONArray)obj; System.out.println("======the 2nd element of array======");

        try {
            Log.d(TAG, "DIO: " + array.get(1));

            JSONObject obj2=(JSONObject)array.get(1);

            System.out.println("======field \"1\"==========");
            System.out.println(obj2.get("1"));

            s="{}"; obj= JSONValue.parse(s); System.out.println(obj);

            s="[5,]"; obj= JSONValue.parse(s); System.out.println(obj);

            s="[5,,2]"; obj=JSONValue.parse(s); System.out.println(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*

        // initialize
        InputStream is = null;
        String result = "";
        JSONObject jObject = null;

        // http post
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        // convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObject = new JSONObject(result);
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        return jObject;
        */
    }

}
