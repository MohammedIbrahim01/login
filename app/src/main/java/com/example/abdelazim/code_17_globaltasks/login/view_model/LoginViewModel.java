package com.example.abdelazim.code_17_globaltasks.login.view_model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.abdelazim.code_17_globaltasks.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class LoginViewModel extends ViewModel implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginViewModel.class.getSimpleName();
    private final MutableLiveData<String> userRole = new MutableLiveData<>();
    private final MutableLiveData<Boolean> signUpRequired = new MutableLiveData<>();

    private GoogleSignInOptions gso;
    private GoogleApiClient googleApiClient;

    public void selectUserRole(String role) {
        userRole.setValue(role);
    }

    public LiveData<String> userRoleChoosen() {
        return userRole;
    }

    public LiveData<Boolean> requireSignUp() {
        return signUpRequired;
    }

    public void registerClicked() {
        signUpRequired.setValue(true);
    }

    public void setupGoogleApiClient(FragmentActivity fragmentActivity) {

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(fragmentActivity.getString(R.string.default_web_client_id))
                .build();
        googleApiClient = new GoogleApiClient.Builder(fragmentActivity)
                .enableAutoManage(fragmentActivity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: fail");
    }
}
