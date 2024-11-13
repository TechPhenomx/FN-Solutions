package com.example.fnsolutions;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class emailLoginFragment extends Fragment {
    private TextInputEditText loginUsername, loginPassword;
    private onEmailDataPass dataPasser;  // Use your defined interface here
    private MaterialButton emailLoginButton;

    public emailLoginFragment() {
        // Required empty public constructor
    }

    public interface onEmailDataPass {
        void onEmailDataPass(String userEmail, String userPassword);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof onEmailDataPass) {  // Check for your interface
            dataPasser = (onEmailDataPass) context; // Set the data passer
        } else {
            throw new ClassCastException(context.toString() + " must implement onEmailDataPass");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_email_login, container, false);

        loginUsername = view.findViewById(R.id.loginUsername);
        loginPassword = view.findViewById(R.id.loginPassword);
        emailLoginButton = view.findViewById(R.id.emailLoginButton);

        //Email Watcher
        loginUsername.addTextChangedListener(phoneWatcher);
        loginPassword.addTextChangedListener(phoneWatcher);

        emailLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = loginUsername.getText().toString().trim() + "@fnsolutions.com";
                String userPassword = loginPassword.getText().toString().trim();

                if (!userEmail.isEmpty() && !userPassword.isEmpty()) {
                    dataPasser.onEmailDataPass(userEmail, userPassword);  // Use dataPasser to pass the data
                } else {
                    // Handle empty fields (e.g., show a toast message)

                }
            }
        });

        return view;
    }

    private final TextWatcher phoneWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String username = loginUsername.getText().toString();
            String userPassword = loginPassword.getText().toString();

//            loginButton.setEnabled(!TextUtils.isEmpty(userPhone) && userPhone.length() == 10);
            emailLoginButton.setEnabled(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(userPassword));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
