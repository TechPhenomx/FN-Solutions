package com.example.fnsolutions;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyNumberFragment extends Fragment {

//    initializing firebase variable
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    private TextInputEditText verifyPhoneNumber;
    private MaterialButton verifyButton;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;

    public VerifyNumberFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verify_number, container, false);

        verifyPhoneNumber = view.findViewById(R.id.verifyPhoneNumber);
        verifyButton = view.findViewById(R.id.verifyButton);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyNumber();
            }
        });
        return view;
    }

    public void verifyNumber(){

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + verifyPhoneNumber.getText().toString().trim())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                VerifyOTPFragment fragment = VerifyOTPFragment.newInstance(mVerificationId, mResendToken);
                                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, fragment);
                                transaction.addToBackStack(null);
                                transaction.commit();

                                Toast.makeText(getContext(), "onVerificationCompleted", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(getContext(), "onVerificationFailed", Toast.LENGTH_SHORT).show();
                                // This callback is invoked in an invalid request for verification is made,
                                // for instance if the the phone number format is not valid.
//                                Log.w(TAG, "onVerificationFailed", e);

                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    // Invalid request
                                } else if (e instanceof FirebaseTooManyRequestsException) {
                                    // The SMS quota for the project has been exceeded
                                } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                                    // reCAPTCHA verification attempted with null Activity
                                }

                                // Show a message and update the UI
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                Toast.makeText(getContext(), "onCodeSent", Toast.LENGTH_SHORT).show();

                                mVerificationId = verificationId;
                                mResendToken = token;

                                VerifyOTPFragment fragment = VerifyOTPFragment.newInstance(mVerificationId, mResendToken);
                                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, fragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
}