package com.estsoft.mblockchain.kow;

import android.*;
import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.estsoft.mblockchain.kow.adapters.GoogleLoginAdapter;
import com.estsoft.mblockchain.kow.adapters.MainPagerAdapter;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;

    /* Added code for Sync Addressbook : 2016-11-23 by 최영진*/
    final int MainActivity_RequestCode = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setupActionBar();
        setupTabs();
        setupFab();

        /* Added code for FCM Push : 2016-11-23 by 최영진*/

        // receive FCM Push by topic
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        // receive Token by each user
        FirebaseInstanceId.getInstance().getToken(); // 이 함수가 불리는 시점에서 DB에 토큰이 저장됨

        /* Added code for Sync Addressbook : 2016-11-23 by 최영진*/
        syncAddressbook();

    }

    /**
     * Sets up the action bar.
     */
    private void setupActionBar() {

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

    }


    private void setupTabs() {
        // Setup view pager
        ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
        viewpager.setAdapter(new MainPagerAdapter(this, getSupportFragmentManager()));
        viewpager.setOffscreenPageLimit(MainPagerAdapter.NUM_ITEMS);
        updatePage(viewpager.getCurrentItem());

        // Setup tab layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewpager);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                updatePage(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }


    private void setupFab() {
        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_grey600_24dp));
        final com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton rightTopButton =
                new com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.Builder(this).
                        setContentView(fabIconNew).build();

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);
        ImageView rlIcon3 = new ImageView(this);
        ImageView rlIcon4 = new ImageView(this);


        rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_chat_light));
        rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_camera_light));
        rlIcon3.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_video_light));
        rlIcon4.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_place_light));

        // Build the menu with default options: light theme, 90 degrees, 72dp radius.
        // Set 4 default SubActionButtons
        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(rLSubBuilder.setContentView(rlIcon1).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon2).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon3).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon4).build())
                .attachTo(rightTopButton)
                .build();

        rlIcon1.setOnClickListener( v ->    {
            Intent inputActivity = new Intent(getApplicationContext(), InputTextActivity.class);
            startActivity(inputActivity);
        } );

        rlIcon2.setOnClickListener( v ->
            Toast.makeText(this,"picture",Toast.LENGTH_LONG).show()
        );
        rlIcon3.setOnClickListener( v ->
            Toast.makeText(this,"voice",Toast.LENGTH_LONG).show()
        );
        rlIcon4.setOnClickListener( v ->
            Toast.makeText(this,"ect",Toast.LENGTH_LONG).show()
        );


        // Listen menu open and close events to animate the button content view
        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }
        });


    }



    private void updatePage(int selectedPage) {
        Toast.makeText(this,"updatePage"+selectedPage,Toast.LENGTH_LONG).show();
    }

    /* Added code for Sync Addressbook : 2016-11-23 by 최영진*/
    private void syncAddressbook() {
        // 요청하고자 하는 권한들
        String[] permissions = new String[] {
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.READ_PHONE_STATE
        };

        // 안드로이드 버전이 마시멜로 이상인지 체크 : 마시멜로 미만에서는 런타임 요청 없이 바로 쿼리를 실행한다.
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {

            ArrayList<String> reqPermissionList = new ArrayList<>();

            // 안드로이드 버전이 마시멜로 이상인 경우
            for ( String permission : permissions ) {
                Log.e("-------onCreate", permission);
                // 권한 허용여부 체크 : 허용 0 반환, 거부 -1 반환
                int result = PermissionChecker.checkSelfPermission(MainActivity.this, permission);

                if ( result == PermissionChecker.PERMISSION_GRANTED ) {
                    // 권한이 허용된 경우
                    Log.e("-------onCreate", permission + "획득");

                } else {
                    // 권한이 거부된 경우
                    reqPermissionList.add(permission);
                }
            }
            getPermissions(reqPermissionList);
        } else {
            // 안드로이드 버전이 마시멜로 미만인 경우
            Log.e("-------onCreate", "마쉬멜로 미만 버전(권한 요청 X)");
        }
    }

    /* Added code for Sync Addressbook : 2016-11-23 by 최영진*/
    // 권한 획득 성공 또는 실패 결과에 따른 처리 해주는 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MainActivity_RequestCode: {
                // If request is cancelled, the result arrays are empty.
                for ( int i = 0; i < permissions.length; i++ ) {
                    if ( grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                        Log.e("onRequestPermissionsRes", permissions[i] + "획득");
                        Log.e("onRequestPermissionsRes", grantResults[i]+"");
                    } else {
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                }

                if ( ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED ||
                        ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_CONTACTS) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_PHONE_STATE)) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setTitle("권한 요청")
                            .setMessage("주소록 동기화를 위해서 권한에 대한 허용이 필요합니다. " +
                                    "\"다시 묻지 않기\"를 체크하시면 이후 \"설정->애플리케이션 관리\"에서 " +
                                    "권한 설정을 할 수 있습니다.")
                            .setCancelable(false) // back 키 터치했을때 다이얼로그 cancel 여부 설정, 디폴트는 true
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialogBuilder.show();
                }
                return;
            }
        }
    }
    /* Added code for Sync Addressbook : 2016-11-23 by 최영진*/
    private void getPermissions(ArrayList<String> reqPermissionList) {
        Log.e("-------getPermission함수", reqPermissionList+"");

        String[] permissions = new String[reqPermissionList.size()];
        for ( int i = 0; i < reqPermissionList.size(); i++ ) {
            permissions[i] = reqPermissionList.get(i);
        }

        if ( reqPermissionList.size() > 0 ) {
            ActivityCompat.requestPermissions(MainActivity.this, permissions, MainActivity_RequestCode);
        } else {
            // 예외처리 : 요청할 권한이 없을 경우
        }
    }


}
