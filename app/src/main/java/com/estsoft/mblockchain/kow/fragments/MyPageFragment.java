package com.estsoft.mblockchain.kow.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.estsoft.mblockchain.kow.LoginActivity;
import com.estsoft.mblockchain.kow.MainActivity;
import com.estsoft.mblockchain.kow.R;
import com.estsoft.mblockchain.kow.adapters.GoogleLoginAdapter;
import com.estsoft.mblockchain.kow.adapters.PreferencesUtil;
import com.google.android.gms.common.api.GoogleApiClient;


/**
 * Created by joeylee on 2016-11-16.
 */

public class MyPageFragment extends Fragment {

    ImageView backgroundImg;    // cover image
    ImageView profileView;  // profile image

    GoogleLoginAdapter loginAdapter;

    public static MyPageFragment newInstance() { return new MyPageFragment(); }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mypage, container, false);

        String acct = PreferencesUtil.getPreferences(getContext(),"acct");

        loginAdapter = new GoogleLoginAdapter(getActivity(), getContext());
        loginAdapter.init();


        // Setup list
        backgroundImg = (ImageView) rootView.findViewById(R.id.header_cover_image);
        profileView = (ImageView) rootView.findViewById(R.id.user_profile_photo);
        Button signOutButton = (Button) rootView.findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener( v -> {

//            Toast.makeText(getContext(),"sign out",Toast.LENGTH_LONG).show();
            loginAdapter.signOut();
            Intent i = new Intent(getContext(), LoginActivity.class);
            startActivity(i);
            getActivity().finish();


        });
        TextView nameView = (TextView) rootView.findViewById(R.id.prof_name);
        TextView profMessage = (TextView)rootView.findViewById(R.id.prof_message);

        nameView.setText(acct);


        return rootView;

    }


}

