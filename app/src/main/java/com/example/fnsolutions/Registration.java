package com.example.fnsolutions;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.aviran.cookiebar2.CookieBar;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Registration extends AppCompatActivity {

    private TextInputEditText registerPhone, register_OTP, registerFullName, registerAddress, registerEmail;
    private Button registerButton, register_verifyOTP;
    private CircularProgressIndicator verifyOtpProgress;
    private MaterialTextView alreadyUser;
    private LinearLayout register_page, verifyNumberAndEmail;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser verifiedUser;
    private ImageButton registerNumberBackButton;
    private RelativeLayout loading_overlay, registrationActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        registrationActivity = findViewById(R.id.registrationActivity);
        loading_overlay = findViewById(R.id.loading_overlay);

//        registerEmail = findViewById(R.id.registerEmail);
        registerFullName = findViewById(R.id.registerFullName);
        registerPhone = findViewById(R.id.registerPhone);
        registerAddress = findViewById(R.id.register_address_edit_text);
        registerButton = findViewById(R.id.register_button);
        alreadyUser = findViewById(R.id.already_user);
        register_page = findViewById(R.id.register_page);
        verifyNumberAndEmail = findViewById(R.id.verifyNumberAndEmail);
        registerNumberBackButton = findViewById(R.id.registerNumberBackButton);
        register_OTP = findViewById(R.id.register_OTP);
        register_verifyOTP = findViewById(R.id.register_verifyOTP);


        //Registration Watcher
        registerFullName.addTextChangedListener(registerWatcher);
        registerPhone.addTextChangedListener(registerWatcher);
        registerAddress.addTextChangedListener(registerWatcher);
//        registerEmail.addTextChangedListener(registerWatcher);
        register_OTP.addTextChangedListener(registerWatcher);


        //click event
        alreadyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registration.this, Login.class));
                finish();
            }
        });

        registerNumberBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_page.setVisibility(View.VISIBLE);
                verifyNumberAndEmail.setVisibility(View.GONE);
            }
        });

        register_verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading_overlay.setVisibility(View.VISIBLE);
                registrationActivity.setVisibility(View.GONE);
                verifyCode(register_OTP.getText().toString());
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading_overlay.setVisibility(View.VISIBLE);
                registrationActivity.setVisibility(View.GONE);

                verifyUserMobileNumber("+91" + registerPhone.getText().toString());
            }
        });
    }

    //Registration Watcher
    private final TextWatcher registerWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String userFullName = registerFullName.getText().toString();
            String userPhone = registerPhone.getText().toString();
            String userAddress = registerAddress.getText().toString();
//            String userEmail = registerEmail.getText().toString();
            String userOTP = register_OTP.getText().toString();

            if (userFullName.length() > 19) {
                registerFullName.setError("Register Name should be less than 20 words");
            }

            registerButton.setEnabled(!TextUtils.isEmpty(userFullName) && !TextUtils.isEmpty(userPhone) && !TextUtils.isEmpty(userAddress));
            register_verifyOTP.setEnabled(!TextUtils.isEmpty(userOTP) && userOTP.length() == 6);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void verifyUserMobileNumber(String phoneNumber) {

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth).setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // (optional) Activity for callback binding
                // If no activity is passed, reCAPTCHA verification can not be used.
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                        Log.w("TAG", "onVerificationFailed", e);
                        Toast.makeText(Registration.this, "onVerificationFailed", Toast.LENGTH_SHORT).show();

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Toast.makeText(Registration.this, "FirebaseAuthInvalidCredentialsException", Toast.LENGTH_SHORT).show();
                            showMessage("Invalid Phone Number");
                            loading_overlay.setVisibility(View.GONE);
                            registrationActivity.setVisibility(View.VISIBLE);
                            verifyNumberAndEmail.setVisibility(View.VISIBLE);

                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            Toast.makeText(Registration.this, "FirebaseTooManyRequestsException", Toast.LENGTH_SHORT).show();
                            showMessage("Requested Too many Times. Please try again Later");
                            loading_overlay.setVisibility(View.GONE);
                            registrationActivity.setVisibility(View.VISIBLE);
                            verifyNumberAndEmail.setVisibility(View.VISIBLE);

                        } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                            // reCAPTCHA verification attempted with null Activity
                            Toast.makeText(Registration.this, "FirebaseAuthMissingActivityForRecaptchaException", Toast.LENGTH_SHORT).show();
                            showMessage("Activity missing, please contact administration");
                            loading_overlay.setVisibility(View.GONE);
                            registrationActivity.setVisibility(View.VISIBLE);
                            verifyNumberAndEmail.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {

                        loading_overlay.setVisibility(View.GONE);
                        registrationActivity.setVisibility(View.VISIBLE);
                        register_page.setVisibility(View.GONE);
                        verifyNumberAndEmail.setVisibility(View.VISIBLE);

                        showMessage("OTP Sent. Please wait !");

                        mVerificationId = verificationId;
                        mResendToken = token;
                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    verifiedUser = task.getResult().getUser();
                    isUserAvailable();

                } else {

                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // Sign in failed, display a message and update the UI
                        loading_overlay.setVisibility(View.GONE);
                        registrationActivity.setVisibility(View.VISIBLE);
                        verifyNumberAndEmail.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    public void createUser(String userFullName, String userPhoneNumber, String userAddress) {

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", userFullName);
        userData.put("phone", userPhoneNumber);
        userData.put("address", userAddress);
        userData.put("createDate", FieldValue.serverTimestamp());
        userData.put("updateDate", FieldValue.serverTimestamp());
        userData.put("userType", "customer");
        userData.put("userStatus", "Inactive");
        userData.put("userID", verifiedUser.getUid());

        db.collection("user").document(verifiedUser.getUid()).set(userData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                FirebaseAuth.getInstance().signOut();
                Intent successfullyRegister = new Intent(Registration.this, Login.class);
                successfullyRegister.putExtra("registerSuccessfully", true);
                startActivity(successfullyRegister);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("TAG", "Error writing document", e);

            }
        });

    }

    public void showMessage(String message) {
        CookieBar.build(Registration.this).setMessage(message).setCookiePosition(CookieBar.TOP).setDuration(4000).show();
    }

    public void isUserAvailable(){
        db.collection("user").document(verifiedUser.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                FirebaseAuth.getInstance().signOut();
                                showMessage("Phone Number is already registered");
                                loading_overlay.setVisibility(View.GONE);
                                registrationActivity.setVisibility(View.VISIBLE);
                                register_page.setVisibility(View.VISIBLE);
                                verifyNumberAndEmail.setVisibility(View.GONE);
                                commonUtilities.clearEditText(register_OTP);
                            } else {
                                createUser(registerFullName.getText().toString(), registerPhone.getText().toString(), registerAddress.getText().toString());
                            }
                        } else {
                            Toast.makeText(Registration.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                            loading_overlay.setVisibility(View.GONE);
                            registrationActivity.setVisibility(View.VISIBLE);
                            register_page.setVisibility(View.VISIBLE);
                            verifyNumberAndEmail.setVisibility(View.GONE);
                            commonUtilities.clearEditText(register_OTP);

                        }
                    }
                });

    }


}