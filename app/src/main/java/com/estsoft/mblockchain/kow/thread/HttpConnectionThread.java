package com.estsoft.mblockchain.kow.thread;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yeonji on 2016-11-21.
 * HttpConnectionThread
 *
 * Setting the request method to POST.
 */

public class HttpConnectionThread extends AsyncTask<String, Void, String> {
    private static String LOG_TAG = "HttpConnectionThread";
    static Context mContext;

    public HttpConnectionThread(Context c) {
        mContext = c;
    }

    @Override
    protected String doInBackground(String... path){
        URL url;
        String response = "";
        String CONNURL = path[0];
        String VALUE = path[1];
        HttpURLConnection conn = null;

        try {
            url = new URL(CONNURL);
            Log.e(LOG_TAG, CONNURL);
            Log.e(LOG_TAG, VALUE);

            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Cache-Control", "no-cache");
//            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();                               // Output Stream to send to server
            os.write(VALUE.getBytes());
            os.flush();
            os.close();         /**  <<<< 위치 */

            Log.e("http response code", conn.getResponseCode()+"");
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {              // Connection success
                Log.e(LOG_TAG, "Connection Success");
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));   // Input stream to receive response for server.

                while ((line = br.readLine()) != null) {
                    response += line;
                }

                br.close();
                conn.disconnect();
                Log.e("RESPONSE", "The response is: " + response);
            }else{
                response = "failed";
            }

        }catch (IOException e) {
            e.printStackTrace();
        }finally{
            conn.disconnect();
        }

        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        // In this method, implement UI update.

        if( result.equals("failed")){
            Log.e("connection","failed");
        }else if( !result.equals("{}")) {
            Log.e("!{}","success");
        }


    }

}   // End_HttpConnectionThread