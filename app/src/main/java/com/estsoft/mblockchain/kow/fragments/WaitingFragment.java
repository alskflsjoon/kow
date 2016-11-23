package com.estsoft.mblockchain.kow.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
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

        // [주소록 동기화 코드]--------------------------------------------------------------------
        // 주소록 가져오는 버튼
        Button bringAddressBook = (Button)rootView.findViewById(R.id.bringAddressBook);
        bringAddressBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetUserContactsList();
            }
        });

        // 자기번호 가져오기
        Button bringMyPhoneNumber = (Button)rootView.findViewById(R.id.bringMyPhoneNumber);
        bringMyPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetUserPhoneNumber();
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

    /* Added code for Sync Addressbook : 2016-11-23 by 최영진*/
    public void GetUserContactsList() {
        // 필요한 권한 : Manifest.permission.READ_CONTACTS;
        // 권한 승인 안받은 경우 예외처리
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED ||
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {

            Log.e("GetUserContactsList", ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)+"");
            // 권한 없음 & 권한 재요청 안내
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("[주소록 엑세스] 권한 요청")
                    .setMessage("주소록 동기화를 위해 [주소록 엑세스] 권한이 필요합니다." +
                            "\"설정->애플리케이션 관리->애플리케이션 관리자 -> 해당 앱 -> 권한\"에서 설정을 바꿀 수 있습니다.")
                    .setCancelable(false)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialogBuilder.show();

            return;
        }

        // 연락처의 [ID, 이름] 을 저장하는 String 배열 변수
        String[] arrProjection = {
                ContactsContract.Contacts._ID, // ID 열에 해당 하는 정보. 저장된 각 사용자는 고유의 ID를 가진다.
                ContactsContract.Contacts.DISPLAY_NAME // 연락처에 저장된 이름 정보.
        };

        // 연락처의 [전화번호] 를 저장하는 String 배열 변수
        String[] arrPhoneProjection = {
                ContactsContract.CommonDataKinds.Phone.NUMBER // 연락처에 저장된 전화번호 정보
        };

        // get user list
        // 주소록에 기록된 연락처 정보 중 ID 와 저장된 이름을 가져오는 Cursor
        // 이후 ID, 저장된 이름에 이어서 email 같은 부가적인 정보를 뒤에 붙인다.
        Cursor clsCursor = getActivity().getContentResolver().query (
                ContactsContract.Contacts.CONTENT_URI,
                arrProjection, // 연락처의 [ID, 이름] 컬럼의 정보를 가져온다.
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1" , // HAS_PHONE_NUMBER : 하나 이상의 전화번호가 있으면 1, 그 외에는 0
                null,
                null
        );
        // Cursor.moveToNext() : Cursor를 다음 행(Row)으로 이동 시킨다. 다음행이 있으면 true, 없으면 false
        // 더이상 출력할 연락처 정보가 없으면 false를 반환하면 while문을 빠져나간다.
        while( clsCursor.moveToNext() )
        {
            // DB에 정의된 필드의 타입에 의해 int로 설정한 필드는 getInt, String으로 설정한 필드는 getString 으로 가져와야 한다.
            // getInt(int index), getString(int index) ... : 쿼리에 따라 요청된 각 컬럼 순서대로 0, 1, 2.. 이렇게 index가 부여되고 index에 해당하는 컬럼의 값이 int나 String 등의 타입으로 가져와진다.
            String strContactId = clsCursor.getString( 0 );

            Log.d("Unity", "연락처 사용자 ID : " + clsCursor.getString( 0 ));
            Log.d("Unity", "연락처 사용자 이름 : " + clsCursor.getString( 1 ));

            // phone number에 접근하는 Cursor
            Cursor clsPhoneCursor = getActivity().getContentResolver().query (
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrPhoneProjection, // 연락처의 [전화번호] 컬럼의 정보를 가져온다.
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + strContactId, // where 절 : 연락처의 ID와 일치하는 전화번호를 가져온다.
                    null,
                    null
            );

            // 연락처에서 하나의 ID에 핸드폰, 집전화 등 전화번호가 하나 이상 있는 경우 while문으로 모두 출력한다.
            // 더이상 출력할 연락처 번호가 없으면 false를 반환하면 while문을 빠져나간다.
            while( clsPhoneCursor.moveToNext() )
            {
                Log.d("Unity", "연락처 사용자 번호 : " + clsPhoneCursor.getString( 0 ));
            }
            // 전화번호 정보에 접근하는 Cursor 닫는다.
            clsPhoneCursor.close();
        }
        // ID, 이름 정보에 접근하는 Cursor 닫는다.
        clsCursor.close( );
    }

    /* Added code for Sync Addressbook : 2016-11-23 by 최영진*/
    public void GetUserPhoneNumber() {
        // 내 번호를 저장할 변수
        String phoneNum = "아무값도 안들어옴";

        // 필요한 권한 : Manifest.permission.READ_PHONE_STATE;
        // 권한 승인 안받은 경우 예외처리
        if ( ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED ||
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_PHONE_STATE) ) {
            Log.e("GetUserPhoneNumber", ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_PHONE_STATE)+"");
            // 권한 없음 & 권한 재요청 안내
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("[전화 걸기 및 관리] 권한 요청")
                    .setMessage("주소록 동기화를 위해 [전화 걸기 및 관리] 권한이 필요합니다." +
                            "\"설정->애플리케이션 관리->애플리케이션 관리자 -> 해당 앱 -> 권한\"에서 설정을 바꿀 수 있습니다.")
                    .setCancelable(false)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialogBuilder.show();

            return;
        }
        // 내 핸드폰 번호를 가져와서 phoneNum에 저장한다.
        TelephonyManager telManager = (TelephonyManager) getActivity().getSystemService(getActivity().TELEPHONY_SERVICE);
        phoneNum = telManager.getLine1Number();

        // 내 핸드폰 번호를 출력한다.
        TextView myPhoneNumber = (TextView)getActivity().findViewById(R.id.myPhoneNumber);
        myPhoneNumber.setText(phoneNum);

        // 내 핸드폰 번호를 DB에 넣는다.
        final String phoneNumber = phoneNum;
        // 네트워크 exception이 발생해 Thread로 해결
        new Thread() {
            public void run() {
                // OkHttpClient 를 사용하기 위해서는 Manifest에 INTERNET 권한을 추가하고,
                // build.gradle 에 okhttp 컴파일 코드를 추가해야 한다.
                // 참고 : OkHttpClient을 이용한 통신은 3G환경에서는 java.net.SocketTimeoutException 에러가 발생
                //        Wifi 환경에서는 잘됨.
                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("phoneNumber", phoneNumber)
                        .build();

                //request
                Request request = new Request.Builder()
                        // input MAC address of Ethernet
                        .url("http://192.168.22.73/addressbookphp/insertPhoneNumber.php")
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
}