package com.estsoft.mblockchain.kow.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.estsoft.mblockchain.kow.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by joeylee on 2016-11-16.
 */

public class WaitingFragment extends Fragment {

    public static WaitingFragment newInstance() {
        return new WaitingFragment();
    }

    // 2016-11-23 추가 by 최영진
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_waiting, container, false);

        // Setup list
        // 2016-11-23 추가 by 최영진
        /* Added code for FCM Push */

        // wigets for pusing FCM
        Button BringTokenListBTN = (Button)rootView.findViewById(R.id.BringTokenList);
        Button PushByTopicBTN = (Button)rootView.findViewById(R.id.PushByTopic);
        Button PushByTokenBTN = (Button)rootView.findViewById(R.id.PushByToken);

        //★★★★★★★★★★★★★★★★★★★★★★★★★★★★

        Button vibOFF = (Button)rootView.findViewById(R.id.vibOFF);
        Button vibON = (Button)rootView.findViewById(R.id.vibON);

        vibOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                //vib.cancel();

                // 디폴트 = on
                String vibONOFF = "off";

                /*Intent intent = new Intent(MainActivity.this, FirebaseMessagingService.class);
                intent.putExtra("vibONOFF", vibONOFF);
                startService(intent);*/

                Toast.makeText(rootView.getContext(), "진동을 끕니다", Toast.LENGTH_SHORT).show();

            }
        });

        vibON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                //vib.vibrate(5000);

                // 디폴트 = on
                String vibONOFF = "on";

                /*Intent intent = new Intent(MainActivity.this, FirebaseMessagingService.class);
                intent.putExtra("vibONOFF", vibONOFF);
                startService(intent);*/

                Toast.makeText(rootView.getContext(), "진동을 킵니다.", Toast.LENGTH_SHORT).show();
            }
        });
        //★★★★★★★★★★★★★★★★★★★★★★★★★★★★

        // button to bring token list
        BringTokenListBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromPHP dataFromPHP = new getDataFromPHP();
                dataFromPHP.execute("http://192.168.22.73/fcmphp/bringTokenList.php");
            }
        });

        // button to push FCM by topic
        PushByTopicBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(rootView.getContext(), "토픽으로 FCM 보냄", Toast.LENGTH_LONG).show();

                // 네트워크 exception이 발생해 Thread로 해결
                new Thread() {
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        RequestBody body = new FormBody.Builder()
                                .add("way", "topic")
                                .build();

                        //request
                        Request request = new Request.Builder()
                                // input MAC address of Ethernet
                                .url("http://192.168.22.73/fcmphp/FCMPush.php")
                                .post(body)
                                .build();
                        try {
                            client.newCall(request).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        // button to push FCM by token
        PushByTokenBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(rootView.getContext(), "토큰으로 FCM 보냄", Toast.LENGTH_LONG).show();

                // 네트워크 exception이 발생해 Thread로 해결
                new Thread() {
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        RequestBody body = new FormBody.Builder()
                                .add("way", "token")
                                .build();

                        //request
                        Request request = new Request.Builder()
                                // input MAC address of Ethernet
                                .url("http://192.168.22.73/fcmphp/FCMPush.php")
                                .post(body)
                                .build();

                        try {
                            client.newCall(request).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });


        return rootView;
    }

    // PHP에서 데이터 가져오는 클래스
    private class getDataFromPHP extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... urls) {

            StringBuilder jsonHtml = new StringBuilder();
            try{
                // 연결 url 설정
                URL url = new URL(urls[0]);
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                // 연결되었으면.
                if(conn != null){
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for(;;){
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if(line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
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

        protected void onPostExecute(String str) {
            try {
                // 파싱한 JSON 데이터를 저장
                String tokens = "";

                // PHP에서 받아온 JSON 데이터를 JSON오브젝트로 변환
                JSONObject jObject = new JSONObject(str);
                // results라는 key는 JSON배열로 되어있다.
                JSONArray results = jObject.getJSONArray("tokens");

                for ( int i = 0; i < results.length(); ++i ) {
                    JSONObject temp = results.getJSONObject(i);

                    tokens += temp.get("no");
                    tokens += " : ";
                    tokens += temp.get("token");
                    tokens += "\n\n";
                }
                // 프래그먼트에서는 findViewByID를 getView() 함수에서 가져온다.
                TextView TokenListTV = (TextView)getView().findViewById(R.id.TokenList);
                TokenListTV.setText(tokens);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

