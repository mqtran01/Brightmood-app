package com.github.mqtran01.brightmood_android;

/**
 * Created by florali on 13/10/17.
 */

import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class LogicFitbit {
    public String logicFitbit(TextView tv) {
        tv.setText("Into Logic Fitbit");
        HttpHandler sh = new HttpHandler(tv);
        // Making a request to url and getting response

        String url = "https://api.fitbit.com/1/user/-/activities/heart/date/today/1d/1sec/time/00:00/00:02.json";
//        String url = "https://www.google.com.au";
        String jsonStr = sh.makeServiceCall(url);

        //Log.e(TAG, "Response from url: " + jsonStr);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONObject jsonChild = new JSONObject("activities-heart-intraday");
                // Getting JSON Array node
                // looping through All datas
                JSONArray jsonArray = new JSONArray(jsonChild.getJSONArray("dataset"));
                int sum1 = 0;
                int sum2 = 0;
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject c = jsonArray.getJSONObject(i);
                    String time = c.getString("time");
                    int value = c.getInt("value");
                    if (i < jsonArray.length() / 2)
                        sum1 += value;
                    else
                        sum2 += value;
                }
                if (sum2 + 10 > sum1) // not should be rigid but maybe >10
                    return "a=8&c=0&l=100";
                else
                    return "a=8&c=0&l=0";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "got json not null";
        }else {
            return null;
        }
    }
}
