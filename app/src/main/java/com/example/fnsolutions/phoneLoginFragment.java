package com.example.fnsolutions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class phoneLoginFragment extends Fragment {
    private TextInputEditText loginPhone;
    private MaterialButton loginButton;
    private OnDataPass dataPasser;

    public phoneLoginFragment() {
        // Required empty public constructor
    }

    public interface OnDataPass {
        void onDataPass(String data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDataPass) {
            dataPasser = (OnDataPass) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement OnDataPass");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_phone_login, container, false);

        loginPhone = view.findViewById(R.id.loginPhone);
        loginButton = view.findViewById(R.id.loginButton);


        //phone Number Watcher
        loginPhone.addTextChangedListener(phoneWatcher);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginPhone.clearFocus();

                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(loginPhone.getWindowToken(), 0);
                }


                String phoneNumber = loginPhone.getText().toString();

                if (!TextUtils.isEmpty(loginPhone.getText())) {
                    dataPasser.onDataPass(phoneNumber);
                } else {
                    // Handle empty phone number case (e.g., show a toast)

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

            String userPhone = loginPhone.getText().toString();

//            loginButton.setEnabled(!TextUtils.isEmpty(userPhone) && userPhone.length() == 10);
            loginButton.setEnabled(!TextUtils.isEmpty(userPhone) && userPhone.length() == 10);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}