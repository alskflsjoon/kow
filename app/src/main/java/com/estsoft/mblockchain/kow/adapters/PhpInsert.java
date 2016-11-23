package com.estsoft.mblockchain.kow.adapters;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by joeylee on 2016-11-23.
 */

public class PhpInsert extends AsyncTask<String, Integer, String> {


    @Override
    protected String doInBackground(String... urls) {

        StringBuilder jsonHtml = new StringBuilder();
        try{
            // 연결 url 설정
            URL url = new URL(urls[0]);

            String email = urls[1];
            String name = urls[2];
            String id = urls[3];
            Log.d("aaa", url.toString());
            Log.d("aaa", email);
            Log.d("aaa", name);
            Log.d("aaa", id);
            // 커넥션 객체 생성
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            // 연결되었으면.
            if(conn != null) {

                conn.setDefaultUseCaches(false);
                conn.setDoInput(true);                 // 서버에서 읽기 모드 지정
                conn.setDoOutput(true);                // 서버로 쓰기 모드 지정
                conn.setRequestMethod("POST");         // 전송 방식은 POST
                conn.setConnectTimeout(10000);
                conn.setUseCaches(false);

                // 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다
                conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
//                conn.setRequestProperty("Content-Type", "text/html");
//                conn.setRequestProperty("Content-Type", "application/json");
                // 연결되었음 코드가 리턴되면.

                StringBuffer buffer = new StringBuffer();
                buffer.append("email").append("=").append(email).append("&").append("name").append("=").append(name).
                        append("&").append("id").append("=").append(id);

                OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");

                PrintWriter writer = new PrintWriter(outStream);
                writer.write(buffer.toString());
                writer.flush();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    for (; ; ) {
                        // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                        String line = br.readLine();
                        if (line == null) break;
                        // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                        jsonHtml.append(line + "\n");
                        //Log.d("abc5", jsonHtml.toString());
                    }
                    br.close();
                }
                conn.disconnect();
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return jsonHtml.toString();
    }



    @Override
    protected void onPostExecute(String str) {
        super.onPostExecute(str);
    }

}


