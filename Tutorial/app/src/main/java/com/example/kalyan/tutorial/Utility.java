package com.example.kalyan.tutorial;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by KALYAN on 26-01-2018.
 */

public class Utility {

    public static  String getMainContentFromWiki(String jsonResponce){
        try {
            JSONArray rootArray = new JSONArray(jsonResponce);
            JSONArray contentArray = rootArray.getJSONArray(2);
            String retStr = contentArray.getString(0);
            return retStr;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMainContentDataForWeather(String jsonResponce){
        try {
            JSONObject root = new JSONObject(jsonResponce);
            JSONObject headlineObject = root.getJSONObject("Headline");
            String overalText = headlineObject.getString("Text");

            JSONArray DailyForecastsArray = root.getJSONArray("DailyForecasts");

            JSONObject DailyForecastsObject = DailyForecastsArray.getJSONObject(0);

            JSONObject tempObject = (JSONObject) DailyForecastsObject.get("Temperature");
            JSONObject dayObject = (JSONObject) DailyForecastsObject.get("Day");
            JSONObject nightObject = (JSONObject) DailyForecastsObject.get("Night");

            JSONObject minObject = tempObject.getJSONObject("Minimum");
            JSONObject maxObject = tempObject.getJSONObject("Maximum");

            String minVal = minObject.getString("Value");
            String maxVal = maxObject.getString("Value");

            int dayIcon = dayObject.getInt("Icon");
            String dayPhrase = dayObject.getString("IconPhrase");

            int nightIcon = nightObject.getInt("Icon");
            String nightPhrase = nightObject.getString("IconPhrase");

            return overalText+"/"+minVal+"/"+maxVal+"/"+dayIcon+"/"+dayPhrase+"/"+nightIcon+"/"+nightPhrase;
        } catch (JSONException e) {
            Log.e("MaincontentForWeather",e.toString());
        }
        return null;
    }

    public static String getLocationCode(String jsonResponce){
        try {
            JSONArray root =  new JSONArray(jsonResponce);
            JSONObject keyObject = (JSONObject) root.get(0);
            String key = keyObject.getString("Key");
            Log.e("LocationCode",key);
            return key;
        } catch (JSONException e) {
            Log.e("getLocationCode",e.toString());
        }
        return  null;
    }
    public static float convertFToC(float f){
        float  c = (float) ((5.0/9.0)*(f-32));
        return c;
    }

    public static String getResponceForUrl(String url){
        URL codeUrl = null;
        String responce = null;
        try {
            codeUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) codeUrl.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            Log.e(urlConnection.getResponseMessage(),"");

            InputStream inputStream = urlConnection.getInputStream();
            responce = Utility.readFromStream(inputStream);
        } catch (MalformedURLException e) {
           Log.e("getResponceForUrl",e.toString());
        } catch (ProtocolException e) {
            Log.e("getResponceForUrl",e.toString());
        } catch (IOException e) {
            Log.e("getResponceForUrl",e.toString());
        }
        return  responce;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
