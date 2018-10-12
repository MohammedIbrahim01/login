package com.example.abdelazim.code_17_globaltasks.login.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdelazim.code_17_globaltasks.User;
import com.example.abdelazim.code_17_globaltasks.login.view_model.LoginViewModel;
import com.example.abdelazim.code_17_globaltasks.main.MainActivity;
import com.example.abdelazim.code_17_globaltasks.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    private EditText nameEditText, emailEditText, passwordEditText;
    private TextView premiumNormalSignUpTextView;
    private ImageView premiumNormalUserImageView;
    private Button signUpButton;
    private View loginForm, loginProgress;
    private LoginViewModel viewModel;
    // Firebase instances
    FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference usersNode;

    private String mUserRole;


    public SignUpFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sign_up_layout, container, false);

        loginForm = view.findViewById(R.id.login_form);
        loginProgress = view.findViewById(R.id.login_progress);
        nameEditText = view.findViewById(R.id.name_editText);
        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
        premiumNormalSignUpTextView = view.findViewById(R.id.premium_normal_sign_up_textView);
        premiumNormalUserImageView = view.findViewById(R.id.normal_premium_user_imageView);
        signUpButton = view.findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(this);

        viewModel = ViewModelProviders.of(getActivity()).get(LoginViewModel.class);

        viewModel.userRoleChoosen().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String userRole) {
                viewModel.userRoleChoosen().removeObserver(this);
                if (userRole != null) {
                    mUserRole = userRole;
                    String signUpText = userRole.substring(0, 1).toUpperCase() + userRole.substring(1) + " sign up :";
                    premiumNormalSignUpTextView.setText(signUpText);
                    premiumNormalUserImageView.setImageResource(userRole.equals("premium") ? R.drawable.user_premium : R.drawable.user_normal);
                }
            }
        });

        showProgress(false);

        // Firebase Auth setup
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        usersNode = databaseReference.child("users");

        return view;
    }


    private void attemptSignUp() {

        // Reset errors.
        emailEditText.setError(null);
        passwordEditText.setError(null);

        // Store values at the time of the login attempt.
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordEditText.setError(getString(R.string.error_invalid_password));
            focusView = passwordEditText;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(getString(R.string.error_field_required));
            focusView = emailEditText;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailEditText.setError(getString(R.string.error_invalid_email));
            focusView = emailEditText;
            cancel = true;
        }
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError(getString(R.string.error_field_required));
            focusView = nameEditText;
            cancel = true;
        } else if (!isNameValid(name)) {
            nameEditText.setError(getString(R.string.error_invalid_name));
            focusView = nameEditText;
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

            signUp(name, email, password);
        }
    }

    private boolean isNameValid(String name) {

        char[] chars = name.toCharArray();

        for (char c : chars) {
            if(!Character.isLetter(c)) {
                return false;
            }
        }

        return true;
    }


    private boolean isEmailValid(String email) {

        return email.contains("@") && email.contains(".com");
    }

    private boolean isPasswordValid(String password) {

        return password.length() >= 8;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up_button:

                attemptSignUp();
                break;
            default:
        }
    }

    private void signUp(final String name, String email, String password) {

        showProgress(true);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            usersNode.child(firebaseAuth.getUid()).setValue(new User(name, mUserRole));
                            getActivity().finish();
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        } else
                            Toast.makeText(getActivity(), "sign up failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showProgress(boolean show) {

        loginProgress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        loginForm.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
    }
}
