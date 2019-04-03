package com.jasong.nycschools;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/*
 * GetJsonFromApiTask: Retrieves JSON from specified endpoint and returns a JSONArray containing the data
 */
public class GetJsonFromApiTask extends AsyncTask<String, Void, JSONArray> {
    MainActivity activity;
    String endpoint;

    public GetJsonFromApiTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        try {
            // Get the endpoint from the call (only supporting 1 URL at a time presently
            this.endpoint = params[0];
            URL apiUrl = new URL(this.endpoint);

            // Create a BufferedReader to read in the data
            BufferedReader in = new BufferedReader(new InputStreamReader(apiUrl.openStream()));

            // Create a StringBuilder to append the incoming data to
            StringBuilder sb = new StringBuilder();

            // Iterate over each line of the data and add it to the StringBuilder
            String currentLine;
            while ((currentLine = in.readLine()) != null) {
                sb.append(currentLine);
            }

            // Create a JSONArray from the API result
            JSONArray json = new JSONArray(sb.toString());

            in.close();

            return json;
        } catch(Exception e) {
            Log.e("NYCSchools","The following exception occurred while downloading from the NYCSchools API: " + e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONArray result) {
        // Executes the callback method on the Activity
        this.activity.apiDownloadCompleted(result, this.endpoint);
    }
}
