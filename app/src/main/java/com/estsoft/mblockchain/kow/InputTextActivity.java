package com.estsoft.mblockchain.kow;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.estsoft.mblockchain.kow.thread.HttpConnectionThread;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by yeonji on 2016-11-22.
 * This Activity is received user's text input.
 *
 * If the user fill in this document form,
 * we can generate text file(.txt) and save text into DB.
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
            generateTextFile(input);

            new HttpConnectionThread(getApplicationContext()).execute(schInsertURL,  generateConnData(input));
            finish();            // Back to MainActivity
        });


    }

    private String getUserID(){

        /*************************************************************************************/
        String userID = "yjhwo";        // <<< user db와 연동하기!
        /*************************************************************************************/
        return userID;
    }

    private String generateConnData(String input){
        // Set up data for send to ConnectionThread
        String userID = getUserID();
        return "id="+userID+"&text="+input;
    }

    private String generateFileName(){
        // Generate File Name
        // rule : user id + generated num
        String userID = getUserID();

        String fileName = userID + generatedNum+".txt";
        Log.e("To generate fileName", fileName);

        return fileName;
    }

    private void generateTextFile(String input) {

        setFileNum();          // Generate File Number

        try{
            // Genrate txt file
            FileOutputStream fos = openFileOutput(generateFileName(), Context.MODE_WORLD_READABLE);
            fos.write(input.getBytes());            // <<<<<여기 수정
            fos.close();

            Toast.makeText(getApplicationContext(), "Save Success", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setFileNum() {

        // Get user's file list in /data/data/package name/files/...

        String dirPath = getFilesDir().getPath();
        File file = new File(dirPath);
        Log.e("listFiles.length",file.listFiles().length+"");

        // If received file is bigger than 1, print out the file name.
        File[] list = file.listFiles();

        for (File f : list) {
            if( f.isFile() ) {                  // only file
                String str = f.getName();

                if( str.contains(".txt") ){     // only '.txt' file
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
