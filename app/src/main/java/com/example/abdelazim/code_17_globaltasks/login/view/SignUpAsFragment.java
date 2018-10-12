package com.example.abdelazim.code_17_globaltasks.login.view;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.abdelazim.code_17_globaltasks.R;
import com.example.abdelazim.code_17_globaltasks.login.view_model.LoginViewModel;

public class SignUpAsFragment extends Fragment implements View.OnClickListener {

    private ImageButton normalUserImageButton, premiumUserImageButton;
    private LoginViewModel viewModel;

    public SignUpAsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sign_up_as_layout, container, false);

        viewModel = ViewModelProviders.of(getActivity()).get(LoginViewModel.class);

        normalUserImageButton = view.findViewById(R.id.normal_user_imageButton);
        premiumUserImageButton = view.findViewById(R.id.premium_user_imageButton);
        normalUserImageButton.setOnClickListener(this);
        premiumUserImageButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.normal_user_imageButton:
                viewModel.selectUserRole("normal");
                break;
            case R.id.premium_user_imageButton:
                viewModel.selectUserRole("premium");
                break;
        }
    }
}
