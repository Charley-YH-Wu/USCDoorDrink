package com.example.uscdrinkdoor;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class TimeParser extends AsyncTask<String, String, String> {
    TaskLoadedCallback taskCallback;
    String directionMode = "driving";

    public TimeParser(Context mContext, String directionMode) {
        this.taskCallback = (TaskLoadedCallback) mContext;
        this.directionMode = directionMode;
    }

    @Override
    protected String doInBackground(String... jsonData) {

        JSONObject jObject;
        String out = "";
        try {
            jObject = new JSONObject(jsonData[0]);
            Log.d("mylog", jsonData[0].toString());
            DataParser parser = new DataParser();
            Log.d("mylog", parser.toString());
            // Starts parsing data
            out = parser.parsetime(jObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return out;
    }
    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(String result) {
        taskCallback.onSecondTaskDone(result);
    }
}
