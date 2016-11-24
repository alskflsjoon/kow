package com.estsoft.mblockchain.kow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


import com.estsoft.mblockchain.kow.adapters.GoogleLoginAdapter;
import com.estsoft.mblockchain.kow.adapters.PreferencesUtil;


/**
 * Created by joeylee on 2016-11-16.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{




    GoogleLoginAdapter loginAdapter = GoogleLoginAdapter.newInstance(this, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);





        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        loginAdapter.init();


    }


    @Override
    protected void onStart() {
        super.onStart();

        loginAdapter.autoLogin();

    }


    //onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == loginAdapter.RC_SIGN_IN) {
            Intent i = loginAdapter.onActivityResult(data);

            startActivity(i);
            finish();

        }

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }

    }


    private void signIn() {

        startActivityForResult(loginAdapter.signIn(), loginAdapter.RC_SIGN_IN);
    }


}

