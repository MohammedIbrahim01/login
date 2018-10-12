package com.example.abdelazim.code_17_globaltasks.login.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.abdelazim.code_17_globaltasks.R;
import com.example.abdelazim.code_17_globaltasks.login.view_model.LoginViewModel;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    private static final String TAG = LoginActivity.class.getSimpleName();

    // FragmentManager
    private FragmentManager fragmentManager;
    // Fragment transaction
    private FragmentTransaction transaction;
    // Shared ViewModel
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get the fragment manager
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        // Get the shared ViewModel
        viewModel = ViewModelProviders.of(LoginActivity.this).get(LoginViewModel.class);
        // Setup GoogleApiClient
        viewModel.setupGoogleApiClient(this);

        // Display signIn fragment
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .add(R.id.login_fragments_container, new SignInFragment())
                .commit();


        // Observe requireSignUp
        viewModel.requireSignUp().observe(LoginActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Log.i(TAG, "onChanged: requireSignUp");
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_down, R.anim.slide_in_down, R.anim.slide_out_top)
                        .replace(R.id.login_fragments_container, new SignUpAsFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Observe userRole
        viewModel.userRoleChoosen().observe(LoginActivity.this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String userRole) {
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.login_fragments_container, new SignUpFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

    }
}

