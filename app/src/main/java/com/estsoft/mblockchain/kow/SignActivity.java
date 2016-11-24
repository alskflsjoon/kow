package com.estsoft.mblockchain.kow;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;


public class SignActivity extends AppCompatActivity implements View.OnClickListener{


    public final int APP_PERMISSION_STORAGE = 1;

    Button save_btn;
    Button refresh_btn;
    Button test_btn;
    Paper sign_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        save_btn = (Button) findViewById(R.id.save_btn);
        refresh_btn = (Button) findViewById(R.id.refresh_btn);
        test_btn = (Button) findViewById(R.id.test_btn);
        sign_view = (Paper) findViewById(R.id.sign_view);

        save_btn.setOnClickListener(this);
        refresh_btn.setOnClickListener(this);
        test_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {


        Intent i = null;

        switch (v.getId()) {

            case R.id.save_btn:

                checkPermission();

                break;

            case R.id.refresh_btn:
                //refresh
                Log.d("aa", "aa");


                this.runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {


                                sign_view.clearCanvas();
                                Log.d("bb", "bb");
                            }
                        });
//                sign_view.invalidateWhole();

                break;

            case R.id.test_btn:

                i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();

                break;
        }

    }

    public void MakeCache(View v, String filename) {

        String StoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.d("aaa", StoragePath);
        String savePath = StoragePath + "/mbc";
        Log.d("aaa", savePath);
        File f = new File(savePath);
        if (!f.isDirectory()) {
            f.mkdirs();
        }

        //v.buildDrawingCache();

        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);

        Bitmap captureView = Bitmap.createBitmap(v.getMeasuredWidth(),
                v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas screenShotCanvas = new Canvas(captureView);
        Drawable bgDrawable = v.getBackground();
        if(bgDrawable!= null) {
            bgDrawable.draw(screenShotCanvas);
        }
        else {
            //screenShotCanvas.drawColor(Color.WHITE);
            screenShotCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }
        v.draw(screenShotCanvas);

        //Bitmap bitmap = v.getDrawingCache();
        FileOutputStream fos;
        try {

            fos = new FileOutputStream(savePath + "/" + filename);
            Log.d("[screenshot]"," : " + v.getDrawingCache());
            captureView.compress(Bitmap.CompressFormat.PNG, 100, fos);
            v.setDrawingCacheEnabled(false);
            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_LONG).show();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case APP_PERMISSION_STORAGE:

                //권한이 있는 경우
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                }
                //권한이 없는 경우
                else {
                    Toast.makeText(this, "Permission always deny", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void checkPermission() {

        //권한이 없는 경우
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            //최초 거부를 선택하면 두번째부터 이벤트 발생 & 권한 획득이 필요한 이융를 설명
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
            }

            //요청 팝업 팝업 선택시 onRequestPermissionsResult 이동
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    APP_PERMISSION_STORAGE);

        }
        //권한이 있는 경우
        else {

            MakeCache(sign_view, "my_sign.png");


        }


    }


}
