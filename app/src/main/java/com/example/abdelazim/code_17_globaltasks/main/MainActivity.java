package com.example.abdelazim.code_17_globaltasks.main;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.abdelazim.code_17_globaltasks.R;
import com.example.abdelazim.code_17_globaltasks.login.view.LoginActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String ANONYMOUS = "anonymous";
    private static final String TAG = MainActivity.class.getSimpleName();
    private String userName = ANONYMOUS;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (firebaseUser == null) {

            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            userName = firebaseUser.getDisplayName();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                firebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(googleApiClient);
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed: failed");
    }
}
