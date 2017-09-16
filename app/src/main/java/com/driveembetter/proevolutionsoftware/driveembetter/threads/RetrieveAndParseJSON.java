package com.driveembetter.proevolutionsoftware.driveembetter.threads;

import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.exceptions.CallbackNotInitialized;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.COUNTRY;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.REGION;
import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.SUB_REGION;

/**
 * Created by alfredo on 13/09/17.
 */

public class RetrieveAndParseJSON implements Runnable {

    private final static String TAG = RetrieveAndParseJSON.class.getSimpleName();

    // Resources
    private final static String URLResource = "http://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=true";
    private final double latitude, longitude;
    private final CallbackRetrieveAndParseJSON callback;

    // Header key
    private final static String KEY_HEADER_LANGUAGE = "Accept-Language";
    private final static String VALUE_HEADER_LANGUAGE = "it-IT";

    // Key of JSON object file to receive from Google's geocoder
    private final static String KEY_STATUS_RESPONSE = "status";
    private final static String VALUE_STATUS_OK = "OK";
    private final static String KEY_RESULT_OBJ = "results";
    private final static String KEY_ADDRESS_OBJ = "address_components";
    private final static String KEY_LONG_NAME = "long_name";
    private final static String KEY_TYPES = "types";
    private final static String VALUE_TYPES_COUNTRY = "country";
    private final static String VALUE_TYPES_REGION = "administrative_area_level_1";
    private final static String VALUE_TYPES_SUB_REGION = "administrative_area_level_2";

    public interface CallbackRetrieveAndParseJSON {
        void onDataComputed(String[] position);
    }

    public RetrieveAndParseJSON(CallbackRetrieveAndParseJSON callback,
                                double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        if (callback == null) {
            throw new CallbackNotInitialized(TAG);
        } else {
            this.callback = callback;
        }
    }



    @Override
    public void run() {
        this.callback.onDataComputed(
                this.getPositionFromJSON(this.latitude, this.longitude)
        );
    }

    private boolean areItemsNotNull(String[] string) {
        return string[0] != null && string[1] != null && string[2] != null;
    }

    private String[] getPositionFromJSON(double latitude, double longitude) {
        String[] position = new String[3];
        JSONObject jsonObjectResponse;
        try {
            // See example response from Google API below
            jsonObjectResponse = new JSONObject(
                    this.retrieveJSONFromCoordinates(latitude, longitude)
            );
            Log.d(TAG, "RES: " + jsonObjectResponse.getString(KEY_STATUS_RESPONSE));

            if (jsonObjectResponse.getString(KEY_STATUS_RESPONSE).equals(VALUE_STATUS_OK)) {
                JSONArray jsonArrayResponse = jsonObjectResponse.getJSONArray(KEY_RESULT_OBJ);

                // Check all result type until position array is filled
                for (int i = 0; i < jsonObjectResponse.length() && !this.areItemsNotNull(position); ++i) {
                    JSONObject jsonObjectPosition = jsonArrayResponse.getJSONObject(i);
                    JSONArray jsonArrayAddress = jsonObjectPosition.getJSONArray(KEY_ADDRESS_OBJ);

                    // In address component find country / region /sub region
                    for (int j = 0; j < jsonArrayAddress.length(); ++j) {
                        JSONObject keyPosition = jsonArrayAddress.getJSONObject(j);
                        JSONArray jsonArrayType = keyPosition.getJSONArray(KEY_TYPES);
                        // It the first position there should be the type of the component address
                        String levelPosition = jsonArrayType.getString(0);

                        // Put the value in the array as Country / Region / Sub Region
                        switch (levelPosition) {
                            case VALUE_TYPES_COUNTRY:
                                position[0] = keyPosition.getString(KEY_LONG_NAME);
                                break;

                            case VALUE_TYPES_REGION:
                                position[1] = keyPosition.getString(KEY_LONG_NAME);
                                break;

                            case VALUE_TYPES_SUB_REGION:
                                position[2] = keyPosition.getString(KEY_LONG_NAME);
                                break;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (position[0] == null) {
            position[0] = COUNTRY;
        }
        if (position[1] == null) {
            position[1] = REGION;
        }
        if (position[2] == null) {
            position[2] = SUB_REGION;
        }

        return position;
    }

    private String retrieveJSONFromCoordinates(double latitude, double longitude) {
        HttpURLConnection connection = null;
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = null;

        try {
            URL url = new URL(String.format(Locale.ENGLISH, URLResource, latitude, longitude));

            connection = (HttpURLConnection) url.openConnection();
            // "en-GB,en;q=0.7" or similar
            connection.setRequestProperty(KEY_HEADER_LANGUAGE, VALUE_HEADER_LANGUAGE);
            connection.connect();

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return buffer.toString();
    }

    /*
{
   "results" : [
      {
         "address_components" : [
            {
               "long_name" : "58",
               "short_name" : "58",
               "types" : [ "street_number" ]
            },
            {
               "long_name" : "Strada Provinciale San Filippo",
               "short_name" : "Strada Provinciale S. Filippo",
               "types" : [ "route" ]
            },
            {
               "long_name" : "San Filippo",
               "short_name" : "San Filippo",
               "types" : [ "locality", "political" ]
            },
            {
               "long_name" : "Anagni",
               "short_name" : "Anagni",
               "types" : [ "administrative_area_level_3", "political" ]
            },
            {
               "long_name" : "Provincia di Frosinone",
               "short_name" : "FR",
               "types" : [ "administrative_area_level_2", "political" ]
            },
            {
               "long_name" : "Lazio",
               "short_name" : "Lazio",
               "types" : [ "administrative_area_level_1", "political" ]
            },
            {
               "long_name" : "Italia",
               "short_name" : "IT",
               "types" : [ "country", "political" ]
            },
            {
               "long_name" : "03012",
               "short_name" : "03012",
               "types" : [ "postal_code" ]
            }
         ],
         "formatted_address" : "Strada Provinciale S. Filippo, 58, 03012 San Filippo FR, Italia",
         "geometry" : {
            "location" : {
               "lat" : 41.7699794,
               "lng" : 13.1486406
            },
            "location_type" : "ROOFTOP",
            "viewport" : {
               "northeast" : {
                  "lat" : 41.77132838029149,
                  "lng" : 13.1499895802915
               },
               "southwest" : {
                  "lat" : 41.76863041970849,
                  "lng" : 13.1472916197085
               }
            }
         },
         "place_id" : "ChIJvY1LmkFgJRMRgPEd4qeVG2Y",
         "types" : [ "street_address" ]
      },
      {
         "address_components" : [
            {
               "long_name" : "San Filippo",
               "short_name" : "San Filippo",
               "types" : [ "locality", "political" ]
            },
            {
               "long_name" : "Anagni",
               "short_name" : "Anagni",
               "types" : [ "administrative_area_level_3", "political" ]
            },
            {
               "long_name" : "Provincia di Frosinone",
               "short_name" : "FR",
               "types" : [ "administrative_area_level_2", "political" ]
            },
            {
               "long_name" : "Lazio",
               "short_name" : "Lazio",
               "types" : [ "administrative_area_level_1", "political" ]
            },
            {
               "long_name" : "Italia",
               "short_name" : "IT",
               "types" : [ "country", "political" ]
            },
            {
               "long_name" : "03012",
               "short_name" : "03012",
               "types" : [ "postal_code" ]
            }
         ],
         "formatted_address" : "03012 San Filippo FR, Italia",
         "geometry" : {
            "bounds" : {
               "northeast" : {
                  "lat" : 41.7711766,
                  "lng" : 13.1583067
               },
               "southwest" : {
                  "lat" : 41.763365,
                  "lng" : 13.1366629
               }
            },
            "location" : {
               "lat" : 41.7683188,
               "lng" : 13.1426783
            },
            "location_type" : "APPROXIMATE",
            "viewport" : {
               "northeast" : {
                  "lat" : 41.7711766,
                  "lng" : 13.1583067
               },
               "southwest" : {
                  "lat" : 41.763365,
                  "lng" : 13.1366629
               }
            }
         },
         "place_id" : "ChIJn4g9kENgJRMRmgjhGqffbrI",
         "types" : [ "locality", "political" ]
      },
      {
         "address_components" : [
            {
               "long_name" : "Anagni",
               "short_name" : "Anagni",
               "types" : [ "administrative_area_level_3", "political" ]
            },
            {
               "long_name" : "Provincia di Frosinone",
               "short_name" : "FR",
               "types" : [ "administrative_area_level_2", "political" ]
            },
            {
               "long_name" : "Lazio",
               "short_name" : "Lazio",
               "types" : [ "administrative_area_level_1", "political" ]
            },
            {
               "long_name" : "Italia",
               "short_name" : "IT",
               "types" : [ "country", "political" ]
            },
            {
               "long_name" : "03012",
               "short_name" : "03012",
               "types" : [ "postal_code" ]
            }
         ],
         "formatted_address" : "03012 Anagni FR, Italia",
         "geometry" : {
            "bounds" : {
               "northeast" : {
                  "lat" : 41.7924327,
                  "lng" : 13.2461323
               },
               "southwest" : {
                  "lat" : 41.6713203,
                  "lng" : 13.0625429
               }
            },
            "location" : {
               "lat" : 41.7346304,
               "lng" : 13.1509672
            },
            "location_type" : "APPROXIMATE",
            "viewport" : {
               "northeast" : {
                  "lat" : 41.7924327,
                  "lng" : 13.2461323
               },
               "southwest" : {
                  "lat" : 41.6713203,
                  "lng" : 13.0625429
               }
            }
         },
         "place_id" : "ChIJ60C7F5JgJRMRC5P5Z8l7s5w",
         "types" : [ "administrative_area_level_3", "political" ]
      },
      {
         "address_components" : [
            {
               "long_name" : "03012",
               "short_name" : "03012",
               "types" : [ "postal_code" ]
            },
            {
               "long_name" : "Anagni",
               "short_name" : "Anagni",
               "types" : [ "administrative_area_level_3", "political" ]
            },
            {
               "long_name" : "Provincia di Frosinone",
               "short_name" : "FR",
               "types" : [ "administrative_area_level_2", "political" ]
            },
            {
               "long_name" : "Lazio",
               "short_name" : "Lazio",
               "types" : [ "administrative_area_level_1", "political" ]
            },
            {
               "long_name" : "Italia",
               "short_name" : "IT",
               "types" : [ "country", "political" ]
            }
         ],
         "formatted_address" : "03012 Anagni FR, Italia",
         "geometry" : {
            "bounds" : {
               "northeast" : {
                  "lat" : 41.7924309,
                  "lng" : 13.246133
               },
               "southwest" : {
                  "lat" : 41.67132,
                  "lng" : 13.062542
               }
            },
            "location" : {
               "lat" : 41.7346304,
               "lng" : 13.1509672
            },
            "location_type" : "APPROXIMATE",
            "viewport" : {
               "northeast" : {
                  "lat" : 41.7924309,
                  "lng" : 13.246133
               },
               "southwest" : {
                  "lat" : 41.67132,
                  "lng" : 13.062542
               }
            }
         },
         "place_id" : "ChIJhzGBXr5gJRMRIIFbGp5PCRw",
         "types" : [ "postal_code" ]
      },
      {
         "address_components" : [
            {
               "long_name" : "Provincia di Frosinone",
               "short_name" : "FR",
               "types" : [ "administrative_area_level_2", "political" ]
            },
            {
               "long_name" : "Lazio",
               "short_name" : "Lazio",
               "types" : [ "administrative_area_level_1", "political" ]
            },
            {
               "long_name" : "Italia",
               "short_name" : "IT",
               "types" : [ "country", "political" ]
            }
         ],
         "formatted_address" : "Provincia di Frosinone, Italia",
         "geometry" : {
            "bounds" : {
               "northeast" : {
                  "lat" : 41.955142,
                  "lng" : 14.0276484
               },
               "southwest" : {
                  "lat" : 41.3013499,
                  "lng" : 12.9918714
               }
            },
            "location" : {
               "lat" : 41.6576528,
               "lng" : 13.6362715
            },
            "location_type" : "APPROXIMATE",
            "viewport" : {
               "northeast" : {
                  "lat" : 41.955142,
                  "lng" : 14.0276484
               },
               "southwest" : {
                  "lat" : 41.3013499,
                  "lng" : 12.9918714
               }
            }
         },
         "place_id" : "ChIJ0TefmORSJRMRIIE4sppPCQM",
         "types" : [ "administrative_area_level_2", "political" ]
      },
      {
         "address_components" : [
            {
               "long_name" : "Lazio",
               "short_name" : "Lazio",
               "types" : [ "administrative_area_level_1", "political" ]
            },
            {
               "long_name" : "Italia",
               "short_name" : "IT",
               "types" : [ "country", "political" ]
            }
         ],
         "formatted_address" : "Lazio, Italia",
         "geometry" : {
            "bounds" : {
               "northeast" : {
                  "lat" : 42.8387207,
                  "lng" : 14.0276424
               },
               "southwest" : {
                  "lat" : 40.7847377,
                  "lng" : 11.4493842
               }
            },
            "location" : {
               "lat" : 41.6552418,
               "lng" : 12.989615
            },
            "location_type" : "APPROXIMATE",
            "viewport" : {
               "northeast" : {
                  "lat" : 42.8387207,
                  "lng" : 14.0276424
               },
               "southwest" : {
                  "lat" : 40.7847377,
                  "lng" : 11.4493842
               }
            }
         },
         "place_id" : "ChIJNWU6NebuJBMRKYWj8WSQSm8",
         "types" : [ "administrative_area_level_1", "political" ]
      },
      {
         "address_components" : [
            {
               "long_name" : "Italia",
               "short_name" : "IT",
               "types" : [ "country", "political" ]
            }
         ],
         "formatted_address" : "Italia",
         "geometry" : {
            "bounds" : {
               "northeast" : {
                  "lat" : 47.092,
                  "lng" : 18.7975999
               },
               "southwest" : {
                  "lat" : 35.4897,
                  "lng" : 6.6267201
               }
            },
            "location" : {
               "lat" : 41.87194,
               "lng" : 12.56738
            },
            "location_type" : "APPROXIMATE",
            "viewport" : {
               "northeast" : {
                  "lat" : 47.092,
                  "lng" : 18.7975999
               },
               "southwest" : {
                  "lat" : 35.4897,
                  "lng" : 6.6267201
               }
            }
         },
         "place_id" : "ChIJA9KNRIL-1BIRb15jJFz1LOI",
         "types" : [ "country", "political" ]
      }
   ],
   "status" : "OK"
}
     */
}
