package com.example.fnsolutions;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerifyOTPFragment extends Fragment {

    private TextInputEditText VerifyOTP;
    private MaterialButton verifyButton;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    public VerifyOTPFragment() {
        // Required empty public constructor
    }

    public static VerifyOTPFragment newInstance(String mVerificationId, PhoneAuthProvider.ForceResendingToken mResendToken) {
        VerifyOTPFragment fragment = new VerifyOTPFragment();
        Bundle args = new Bundle();
        args.putString("verificationId", mVerificationId);  // Pass verification ID as string
        args.putParcelable("resendToken", mResendToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verify_o_t_p, container, false);
        VerifyOTP = view.findViewById(R.id.VerifyOTP);
        verifyButton = view.findViewById(R.id.verifyButton);

        // Retrieve data from the Bundle
        if (getArguments() != null) {
            mVerificationId = getArguments().getString("verificationId");
            mResendToken = getArguments().getParcelable("resendToken");
        }

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOTP();
            }
        });

        return view;
    }

    public void verifyOTP() {
        String code = VerifyOTP.getText().toString().trim();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser user = task.getResult().getUser();
                    if (user != null) {

                        Toast.makeText(getContext(), "Welcome " + user.getPhoneNumber(), Toast.LENGTH_SHORT).show();

                        VerifyUsernameAndPasswordFragment fragment = VerifyUsernameAndPasswordFragment.newInstance(user);
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                } else {

                    Exception exception = task.getException();
                    if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(getContext(), "Invalid verification code", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle other types of errors
                        Toast.makeText(getContext(), "Sign-in failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });    }
}