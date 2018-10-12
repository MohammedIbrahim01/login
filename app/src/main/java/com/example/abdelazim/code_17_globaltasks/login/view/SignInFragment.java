package com.example.abdelazim.code_17_globaltasks.login.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdelazim.code_17_globaltasks.main.MainActivity;
import com.example.abdelazim.code_17_globaltasks.R;
import com.example.abdelazim.code_17_globaltasks.User;
import com.example.abdelazim.code_17_globaltasks.login.view_model.LoginViewModel;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInFragment extends Fragment implements View.OnClickListener {

    private static final int RC_GOOGLE_SIGN_IN = 9003;
    private static final String TAG = SignInFragment.class.getSimpleName();

    // Firebase instances
    private FirebaseAuth firebaseAuth;
    private GoogleApiClient googleApiClient;
    // UI references.
    private EditText mEmailView, mPasswordView;
    private Button emailSignInButton, emailRegisterButton;
    private SignInButton googleSignInButton;
    private View mProgressView;
    private View mLoginFormView;
    private DatabaseReference databaseReference;
    private DatabaseReference usersNode;

    // ViewModel
    private LoginViewModel viewModel;

    public SignInFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.signin_fragment, container, false);

        // Get the shared viewModel
        viewModel = ViewModelProviders.of(getActivity()).get(LoginViewModel.class);

        // Set up the login form.
        mEmailView = view.findViewById(R.id.email);
        mPasswordView = view.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        emailRegisterButton = view.findViewById(R.id.email_register_button);
        emailSignInButton = view.findViewById(R.id.email_sign_in_button);
        googleSignInButton = view.findViewById(R.id.google_sign_in_button);
        googleSignInButton.setSize(SignInButton.SIZE_WIDE);
        mLoginFormView = view.findViewById(R.id.login_form);
        mProgressView = view.findViewById(R.id.login_progress);

        emailRegisterButton.setOnClickListener(this);
        emailSignInButton.setOnClickListener(this);
        googleSignInButton.setOnClickListener(this);

        // Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        googleApiClient = viewModel.getGoogleApiClient();

        // Firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference();
        usersNode = databaseReference.child("users");

        return view;
    }


    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            // auth with email and password
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(getContext(), MainActivity.class));
                                getActivity().finish();
                            } else {
                                showProgress(false);
                                Log.d(TAG, "onComplete: login with email and password failed" + task.getException());
                                Toast.makeText(getContext(), "email or password isn't valid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    private boolean isEmailValid(String email) {

        return email.contains("@") && email.contains(".com");
    }

    private boolean isPasswordValid(String password) {

        return password.length() >= 8;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }


    private void signInWithGoogle() {

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, RC_GOOGLE_SIGN_IN);
    }

    private void authFirebaseWithGoogleAccount(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            usersNode.child(firebaseAuth.getCurrentUser() == null ? "anonymous" : firebaseAuth.getCurrentUser().getUid()).setValue(new User(firebaseAuth.getCurrentUser().getDisplayName(), "normal"));
                            startActivity(new Intent(getContext(), MainActivity.class));
                            getActivity().finish();
                        } else {
                            Toast.makeText(getContext(), "login with google failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {

                GoogleSignInAccount account = result.getSignInAccount();
                authFirebaseWithGoogleAccount(account);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email_sign_in_button:

                attemptLogin();
                break;

            case R.id.email_register_button:
                Log.i(TAG, "onClick: register needed");
                viewModel.registerClicked();
                break;
            case R.id.google_sign_in_button:

                showProgress(true);
                signInWithGoogle();
                break;
        }
    }
}
