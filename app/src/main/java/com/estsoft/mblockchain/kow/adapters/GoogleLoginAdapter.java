package com.estsoft.mblockchain.kow.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.estsoft.mblockchain.kow.MainActivity;
import com.estsoft.mblockchain.kow.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Created by joeylee on 2016-11-23.
 */

public class GoogleLoginAdapter implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LoginActivity";
    public static final int RC_SIGN_IN = 9001;
    private static GoogleLoginAdapter googleLoginAdapter;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private Context mContext;
    private FragmentActivity mActivity;


    public static GoogleLoginAdapter newInstance(FragmentActivity activity, Context context) {
//        if (googleLoginAdapter == null) {
//            googleLoginAdapter = new GoogleLoginAdapter(activity, context);
//        }
        return new GoogleLoginAdapter(activity, context);
    }

    public GoogleLoginAdapter(FragmentActivity activity, Context context) {

        mContext = context;
        mActivity = activity;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void init() {

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(mActivity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        SignInButton signInButton = (SignInButton)((Activity) mContext).findViewById(R.id.sign_in_button);

//        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        if (signInButton != null) {
            signInButton.setSize(SignInButton.SIZE_WIDE);
            signInButton.setScopes(gso.getScopeArray());
        }
        // [END customize_button]

    }

    public void setSignInButton() {

    }

    public void autoLogin() {

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });

        }
    }

    public Intent handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully
            GoogleSignInAccount acct = result.getSignInAccount();
            Intent i = new Intent(mContext,MainActivity.class);

            PreferencesUtil.setPreferences(mContext,"acct",acct.getDisplayName());

            return i;

        } else {
            // Signed out

        }

        return null;
    }



    public Intent signIn() {
        if (mGoogleApiClient == null) {
            Log.d("aaaaa", "sign in googleapiclient");
        }

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        return signInIntent;

    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                        Log.d("aaaaa", "signout");
                    }
                });
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    public Intent onActivityResult(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

        return  handleSignInResult(result);
    }



}
