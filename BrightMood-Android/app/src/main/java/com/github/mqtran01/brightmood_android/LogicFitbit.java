package com.github.mqtran01.brightmood_android;

/**
 * Created by florali on 13/10/17.
 */

import android.util.Log;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;
public class LogicFitbit {
    public String logicFitbit(TextView tv) {
        tv.setText("Into Logic Fitbit");
        HttpHandler sh = new HttpHandler(tv);
        // Making a request to url and getting response
        Calendar cal = Calendar.getInstance();
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.add(Calendar.MINUTE,-31);
        cal2.add(Calendar.MINUTE,-30);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentTime = sdf.format(cal1.getTime());
        String currentTime1 = sdf.format(cal1.getTime());
        String currentTime2 = sdf.format(cal2.getTime());
        String url = "https://api.fitbit.com/1/user/-/activities/heart/date/today/1d/1sec/time/"+currentTime1+"/"+currentTime2+".json";
        String url1 = "https://api.fitbit.com/1/user/-/activities/heart/date/2017-10-13/1d/1sec/time/23:01/23:02.json";
//  String url = "https://www.google.com.au";


        String jsonStr = sh.makeServiceCall(url);

        //Log.e(TAG, "Response from url: " + jsonStr);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONObject jsonChild = jsonObj.getJSONObject("activities-heart-intraday");
                // Getting JSON Array node
                // looping through All datas
                JSONArray jsonArray = jsonChild.getJSONArray("dataset");

                int sum1 = 0;
                int sum2 = 0;
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject c = jsonArray.getJSONObject(i);
                    String time = c.getString("time");

                    int value = c.getInt("value");
                    //in 30 sec period the sum of heart rate
                    if(value > 90)
                        return value+"http://192.168.1.32/SetDyNet.cgi?a=10&p=2";

                    else
                        return value+"http://192.168.1.32/SetDyNet.cgi?a=10&p=1";

                    //if (i < jsonArray.length() / 2) {
                    //    sum1 += value;
                    //}
                    //else if(i > jsonArray.length() / 2)
                    //    sum2 += value;
                }
                //as the divid jsonArray.length() / 2 is same so compare the sum is same as average
                //if (sum2  > 90)
                //    return "http://192.168.1.32/SetDyNet.cgi?a=10&p=2";

                if (sum2==0 && sum1==0)
                    //have to hardcode
                {   //a random function change in once minutes
                    int a = cal.get(Calendar.MINUTE) % 2;
                    if(a==0)
                        return "http://192.168.1.32/SetDyNet.cgi?a=10&p=2";
                    else
                        return "http://192.168.1.32/SetDyNet.cgi?a=10&p=1";
                }
                else
                    return "http://192.168.1.32/SetDyNet.cgi?a=10&p=1";


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "got json not null";
        }else {
            return null;
        }
    }
}
