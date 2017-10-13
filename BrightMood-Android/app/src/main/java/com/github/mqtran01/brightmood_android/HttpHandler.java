package com.github.mqtran01.brightmood_android;

import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


/**
 * Created by florali on 13/10/17.
 */


public class HttpHandler {

    private static final String TAG = HttpHandler.class.getSimpleName();
    private TextView tv;

    public HttpHandler(TextView tv) {
        tv.setText("Built HTTP Handler");

        this.tv = tv;
    }

    public String makeServiceCall(String reqUrl) {
        tv.setText("Making service call");

        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // read the response
            //conn.setRequestProperty("Request Method", "GET");
            conn.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1WjI0WUIiLCJhdWQiOiIyMjhMUlQiLCJpc3MiOiJGaXRiaXQiLCJ0eXAiOiJhY2Nlc3NfdG9rZW4iLCJzY29wZXMiOiJ3aHIgd251dCB3cHJvIHdzbGUgd3dlaSB3c29jIHdhY3Qgd3NldCB3bG9jIiwiZXhwIjoxNTM5MzUwMTkwLCJpYXQiOjE1MDc4MTg5NDJ9.I4vAPfvtxrnayU0yKs1Sqqg5qxtgRFXCJ4LrowUdLNg");




            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
            tv.setText(response);

        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
            tv.setText("MalformedURLException: " + e.getMessage());

        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
            tv.setText("ProtocolException: " + e.getMessage());

        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
            tv.setText("IOException: " + e.toString());

        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
            tv.setText("Exception: " + e.getMessage());
            e.printStackTrace();

        }
        return response;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}

