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
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Login_Registration_Activity extends AppCompatActivity {
    private LinearLayout loginPage, registerPage, otpContainerLayout;
    private Button buyConnection, loginButton, registerButton, verifyNumberButton, registerVerifyOTP;
    private MaterialTextView alreadyUser, notificationText, registerNotificationText;
    private TextInputEditText registerEmail, registerPassword, registerConfirmPassword, registerPhone, registerOTP, registerFullName, registerAddress;
    private TextInputEditText loginEmail, loginPassword;
    private CircularProgressIndicator loginProgress, registerProgress, verifyNumberProgress, verifyOtpProgress;
    private MaterialCardView loginWrongCredentials, registerNotificationCard;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;


    private boolean isVerifyingNumber =false, isVerifyingOTP = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_registration);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Initialize Firestore

        //Layout
        loginPage = findViewById(R.id.login_page);
        registerPage = findViewById(R.id.register_page);

        //User Login
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.login_password_edit_text);
        loginProgress = findViewById(R.id.loginProgress);
        loginWrongCredentials = findViewById(R.id.loginWrongCredentials);
        loginButton = findViewById(R.id.login_button);
        notificationText = findViewById(R.id.notificationText);
        buyConnection = findViewById(R.id.buyConnectionButton);

        //User Registration
        registerFullName = findViewById(R.id.registerFullName);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        registerConfirmPassword = findViewById(R.id.registerConfirmPassword);
        registerPhone = findViewById(R.id.registerPhone);
        registerOTP = findViewById(R.id.register_OTP);
        registerAddress = findViewById(R.id.register_address_edit_text);
        verifyNumberButton = findViewById(R.id.verifyNumberbutton);
        alreadyUser = findViewById(R.id.already_user);
        registerButton = findViewById(R.id.register_button);
        registerVerifyOTP = findViewById(R.id.register_verifyOTP);
        registerProgress = findViewById(R.id.registerProgress);
        registerNotificationCard = findViewById(R.id.registerNotificationCard);
        registerNotificationText = findViewById(R.id.registerNotificationText);
        otpContainerLayout = findViewById(R.id.otpContainerLayout);
        verifyNumberProgress = findViewById(R.id.verifyNumberProgress);
        verifyOtpProgress = findViewById(R.id.verifyOtpProgress);

        //Registration Watcher
        registerFullName.addTextChangedListener(registerWatcher);
        registerEmail.addTextChangedListener(registerWatcher);
        registerPassword.addTextChangedListener(registerWatcher);
        registerConfirmPassword.addTextChangedListener(registerWatcher);
        registerPhone.addTextChangedListener(registerWatcher);
        registerOTP.addTextChangedListener(registerWatcher);
        registerAddress.addTextChangedListener(registerWatcher);


        //login Watcher
        loginEmail.addTextChangedListener(loginWatcher);
        loginPassword.addTextChangedListener(loginWatcher);

        registerButton.setOnClickListener(view -> {
            String email = registerEmail.getText().toString();
            String password = registerPassword.getText().toString();
            String fullName = registerFullName.getText().toString();
            String address = registerAddress.getText().toString();
            String phone = registerPhone.getText().toString();

            registerProgress.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.GONE);
            createUser(email, password, fullName, address, phone);


        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.setVisibility(View.GONE);
                loginProgress.setVisibility(View.VISIBLE);
                signInUser(loginEmail.getText().toString(), loginPassword.getText().toString());
            }
        });

        verifyNumberButton.setOnClickListener(view -> {
            String phoneNumber = registerPhone.getText().toString();
            if (!TextUtils.isEmpty(phoneNumber)) {
                isVerifyingNumber = true;
                otpContainerLayout.setVisibility(View.VISIBLE);
                verifyNumberProgress.setVisibility(View.VISIBLE);
                verifyNumberButton.setVisibility(View.GONE);
                sendVerificationCode("+91" + phoneNumber);
            } else {
                Toast.makeText(Login_Registration_Activity.this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            }
        });

        registerVerifyOTP.setOnClickListener(view -> {
            String code = registerOTP.getText().toString();
            if (!TextUtils.isEmpty(code) && mVerificationId != null) {
                verifyOtpProgress.setVisibility(View.VISIBLE);
                registerVerifyOTP.setVisibility(View.GONE);
                verifyCode(code);
            } else {
                Toast.makeText(Login_Registration_Activity.this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
            }
        });

        buyConnection.setOnClickListener(view -> togglePages());
        alreadyUser.setOnClickListener(view -> togglePages());
    }

    private void togglePages() {
        if (loginPage.getVisibility() == View.VISIBLE) {
            loginPage.setVisibility(View.GONE);
            registerPage.setVisibility(View.VISIBLE);
        } else {
            loginPage.setVisibility(View.VISIBLE);
            registerPage.setVisibility(View.GONE);
        }
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth).setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // (optional) Activity for callback binding
                // If no activity is passed, reCAPTCHA verification can not be used.
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        Log.d("TAG", "onVerificationCompleted:" + credential);
                        Toast.makeText(Login_Registration_Activity.this, "Number Verified", Toast.LENGTH_SHORT).show();

                        verifyNumberButton.setVisibility(View.VISIBLE);
                        verifyNumberButton.setEnabled(false);
                        verifyNumberProgress.setVisibility(View.GONE);

                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.
                        Log.w("TAG", "onVerificationFailed", e);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Toast.makeText(Login_Registration_Activity.this, "Invalid Phone Number. Please Enter Correct Number", Toast.LENGTH_SHORT).show();
                            Log.e("FirebaseAuth", "Invalid verification code: " + e.getMessage());

                            registerPhone.setError("Invalid Phone Number");
                            verifyNumberProgress.setVisibility(View.GONE);
                            verifyNumberButton.setVisibility(View.VISIBLE);


                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            Toast.makeText(Login_Registration_Activity.this, "This number has been blocked. Try Again after sometimes", Toast.LENGTH_SHORT).show();
                            Log.e("FirebaseTooManyRequestsException", "SMS quota exceeded: " + e.getMessage());

                            registerPhone.setError("This number is blocked");
                            verifyNumberProgress.setVisibility(View.GONE);
                            verifyNumberButton.setVisibility(View.VISIBLE);

                        } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                            // reCAPTCHA verification attempted with null Activity
                            Toast.makeText(Login_Registration_Activity.this, "FirebaseAuthMissingActivityForRecaptchaException", Toast.LENGTH_SHORT).show();
                            Log.e("FirebaseAuthMissingActivity", "Missing activity for reCAPTCHA: " + e.getMessage());
                        }

                        // Show a message and update the UI
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        Toast.makeText(Login_Registration_Activity.this, "Your OTP is on the way", Toast.LENGTH_SHORT).show();

                        // Save verification ID and resending token so we can use them later
                        mVerificationId = verificationId;
                        mResendToken = token;

                        Log.d("verification Id","verification Id" + mVerificationId);
                        Log.d("mResendToken","mResendToken" + mResendToken);
                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = task.getResult().getUser();
                Toast.makeText(Login_Registration_Activity.this, "Number Verified Successfully", Toast.LENGTH_SHORT).show();
                // Optionally proceed to next activity here if required

                verifyOtpProgress.setVisibility(View.GONE);
                verifyNumberButton.setVisibility(View.GONE);
                registerPhone.setEnabled(false);
                registerVerifyOTP.setVisibility(View.GONE);
                registerOTP.setEnabled(false);

            } else {
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(Login_Registration_Activity.this, "Invalid verification code", Toast.LENGTH_SHORT).show();
                    registerVerifyOTP.setVisibility(View.VISIBLE);
                    verifyOtpProgress.setVisibility(View.GONE);

                }
            }
        });
    }

    private void createUser(String email, String password, String fullName, String address, String phone) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                Map<String, Object> userData = new HashMap<>();
                userData.put("email", email);
                userData.put("name", fullName);
                userData.put("phone", phone);
                userData.put("address", address);

                db.collection("users").document(user.getUid()).set(userData).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        user.sendEmailVerification();
                        Toast.makeText(Login_Registration_Activity.this, "User registered in Firestore. Please verify your email.", Toast.LENGTH_SHORT).show();
                        getUserData(user);
                    } else {
//                        Toast.makeText(Login_Registration_Activity.this, "Error registering user in Firestore", Toast.LENGTH_SHORT).show();
                        registerNotificationCard.setVisibility(View.VISIBLE);
                        registerNotificationText.setText("Error registering user");
                        registerNotificationCard.setCardBackgroundColor(Color.parseColor("#E72929"));
                        registerProgress.setVisibility(View.GONE);
                        registerButton.setVisibility(View.VISIBLE);
                    }
                });

            } else {
                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    Toast.makeText(Login_Registration_Activity.this, "User already registered.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Login_Registration_Activity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private final TextWatcher loginWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String userEmail = loginEmail.getText().toString();
            String userPass = loginPassword.getText().toString();

            loginButton.setEnabled(!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPass));
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private final TextWatcher registerWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            registerButton.setEnabled(false);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String userFullName = registerFullName.getText().toString();
            String useremail = registerEmail.getText().toString();
            String userPass = registerPassword.getText().toString();
            String userConfirmPass = registerConfirmPassword.getText().toString();
            String userPhone = registerPhone.getText().toString();
            String userOTP = registerOTP.getText().toString();
            String userAddress = registerAddress.getText().toString();

            if (userFullName.length() > 20) {
                registerFullName.setError("Register Name should be less than 50 words");
            }

            if (!userConfirmPass.equals(userPass)) {
                registerConfirmPassword.setError("Confirm Password is different");
            }

            if (!TextUtils.isEmpty(userPhone)) {
                verifyNumberButton.setEnabled(true);
                verifyNumberButton.setVisibility(View.VISIBLE);
                verifyNumberProgress.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(userOTP)) {
                registerVerifyOTP.setEnabled(true);
                verifyNumberProgress.setVisibility(View.GONE);
            }

            if (userPhone.length() > 10) {
                registerPhone.setError("Please enter valid Number");
            }

            registerButton.setEnabled(!TextUtils.isEmpty(userFullName) && !TextUtils.isEmpty(useremail) && !TextUtils.isEmpty(userPass) && !TextUtils.isEmpty(userConfirmPass) && !TextUtils.isEmpty(userPhone) && !TextUtils.isEmpty(userOTP) && !TextUtils.isEmpty(userAddress));
        }

        @Override
        public void afterTextChanged(Editable s) {
            String userPhone = registerPhone.getText().toString();
            String userOTP = registerOTP.getText().toString();

            if (TextUtils.isEmpty(userPhone)) {
                verifyNumberButton.setEnabled(false);
                verifyNumberButton.setVisibility(View.GONE);
                otpContainerLayout.setVisibility(View.GONE);
            }


        }
    };

    public void getUserData(FirebaseUser user) {
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Toast.makeText(this, "User data retrieved successfully", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "DocumentSnapshot data: " + document.getData());

                    // Extract the name from the document
                    String name = (String) document.getData().get("name");

                    // Create the Intent to move to MainActivity
                    Intent moveToMain = new Intent(Login_Registration_Activity.this, MainActivity.class);
                    // Put the user data into the Intent
                    moveToMain.putExtra("USER_NAME", name);

                    startActivity(moveToMain);
                    finish();
                } else {
                    Toast.makeText(this, "No user document found", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "No such document");
                }
            } else {
                Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "get failed with ", task.getException());
            }
        });
    }

    public void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
                    assert user != null;
//                    getUserData(user);

                    startActivity(new Intent(Login_Registration_Activity.this, MainActivity.class));
                    finish();

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithEmail:failure", task.getException());

                    loginWrongCredentials.setVisibility(View.VISIBLE);
                    notificationText.setText("email or Password is in correct😓");
                    loginProgress.setVisibility(View.GONE);
                    loginButton.setVisibility(View.VISIBLE);
//
                    new Handler().postDelayed(() -> {
                        loginWrongCredentials.setVisibility(View.GONE);
                    }, 3500);
                }
            }
        });
    }
}
