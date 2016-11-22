package com.estsoft.mblockchain.kow;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by yeonji on 2016-11-22.
 * This Activity is received user's text input.
 */

public class InputTextActivity extends AppCompatActivity {

    private static String schInsertURL = "http://192.168.22.72/insertUserDocu.php";
    private int generatedNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_text);

        final EditText edt_input = (EditText)findViewById(R.id.edt_input);
        Button btn_enroll = (Button)findViewById(R.id.btn_enroll);
        TextView txt_date = (TextView)findViewById(R.id.txt_date);

        Date now = new Date();
        DateFormat frm = DateFormat.getDateInstance(DateFormat.LONG);
        txt_date.setText(frm.format(now));

        btn_enroll.setOnClickListener( v -> {
            String input = edt_input.getText().toString();
            Log.e("input", input);

            generateTextFile(input);

            /*************************************************************************************/
            String userID = "yjhwo";        // <<< user db와 연동하기!
            /*************************************************************************************/

            // -- 보낼 데이터
            String data = "id="+userID+"&text="+input;
            new HttpConnectionThread(getApplicationContext()).execute(schInsertURL,  data);

        });


    }

    private void generateTextFile(String input) {

        setFileNum();          // Generate File Number

        // Generate File Name
        // rule : user id + generated num
        String userID = "yjhwo";

        String fileName = userID + generatedNum+".txt";
        Log.e("To generate fileName", fileName);

        try{
            // 파일 생성
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_WORLD_READABLE);
            fos.write(input.getBytes());            // <<<<<여기 수정
            fos.close();

            Toast.makeText(getApplicationContext(), "Save Success", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setFileNum() {

        // 파일들 읽어와서 없는 번호부터 생성해줌
        String dirPath = getFilesDir().getPath();
        File file = new File(dirPath);
        Log.e("dirPath", dirPath);

        // 파일이 1개 이상이면 파일 이름 출력
        Log.e("listFiles.length",file.listFiles().length+"");

        File[] list = file.listFiles();         //폴더가 가진 파일객체를 리스트로 받는다.

        for (File f : list) {
            if( f.isFile() ) {                  // only file
                String str = f.getName();

                if( str.contains(".txt") ){     //확장자명 .txt인 파일만 확인
                    generatedNum++;
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        generatedNum = 0;
    }

}
