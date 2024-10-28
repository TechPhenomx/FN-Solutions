package com.example.fnsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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

import org.aviran.cookiebar2.CookieBar;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Login extends AppCompatActivity implements emailLoginFragment.onEmailDataPass, phoneLoginFragment.OnDataPass {

    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private LinearLayout login_page, forgetPasswordPhoneNumber, forgetPasswordOTPVerifyContainer, forgetPassword_changePasswordContainer;
    private RelativeLayout loginOTPContainer, forgetPasswordContainer, loading_overlay;
    private TabLayout loginTab;
    private ViewPager2 loginViewpager;
    private MaterialTextView loginForgetPassword;
    private MaterialButton loginBuyConnection, loginOTPButton, forgetPasswordVerifyNumberButton, forgetPasswordVerifyOTPButton, forgetPasswordNewPasswordButton;
    private TextInputEditText loginOTP, forgetPhoneNumber, forgetPasswordOTP, forgetPasswordNewPassword, forgetPasswordConfirmNewPassword;
    private ImageButton otpBackButton, forgetPasswordBackButton;
    private Boolean isForgettingPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loading_overlay = findViewById(R.id.loading_overlay);
        loading_overlay.setVisibility(View.GONE);

        login_page = findViewById(R.id.login_page);
        loginOTPContainer = findViewById(R.id.loginOTPContainer);
        loginTab = findViewById(R.id.loginTab);
        loginViewpager = findViewById(R.id.loginViewpager);
        loginOTP = findViewById(R.id.loginOTP);
        loginOTPButton = findViewById(R.id.loginOTPButton);
        loginBuyConnection = findViewById(R.id.loginBuyConnection);
        loginForgetPassword = findViewById(R.id.loginForgetPassword);
        otpBackButton = findViewById(R.id.otpBackButton);
        forgetPasswordContainer = findViewById(R.id.forgetPasswordContainer);
        forgetPasswordBackButton = findViewById(R.id.forgetPasswordBackButton);
        forgetPhoneNumber = findViewById(R.id.forgetPhoneNumber);
        forgetPasswordOTP = findViewById(R.id.forgetPasswordOTP);
        forgetPasswordNewPassword = findViewById(R.id.forgetPasswordNewPassword);
        forgetPasswordVerifyNumberButton = findViewById(R.id.forgetPasswordVerifyNumberButton);
        forgetPasswordVerifyOTPButton = findViewById(R.id.forgetPasswordVerifyOTPButton);
        forgetPasswordNewPasswordButton = findViewById(R.id.forgetPasswordNewPasswordButton);
        forgetPasswordConfirmNewPassword = findViewById(R.id.forgetPasswordConfirmNewPassword);

        forgetPasswordPhoneNumber = findViewById(R.id.forgetPasswordPhoneNumber);
        forgetPasswordOTPVerifyContainer = findViewById(R.id.forgetPasswordOTPVerifyContainer);
        forgetPassword_changePasswordContainer = findViewById(R.id.forgetPassword_changePasswordContainer);

//        Login Watcher
        loginOTP.addTextChangedListener(loginWatcher);
        forgetPhoneNumber.addTextChangedListener(loginWatcher);
        forgetPasswordOTP.addTextChangedListener(loginWatcher);
        forgetPasswordNewPassword.addTextChangedListener(loginWatcher);
        forgetPasswordConfirmNewPassword.addTextChangedListener(loginWatcher);

        Intent intent = getIntent();
        boolean isActive = intent.getBooleanExtra("registerSuccessfully", false);

        if (isActive) {
            showMessage("Network provider will contact you shortly. Please Wait !", "You have Successfully Registered 🎉");
        }

        //setup Tab layout with viewPager2
        loginViewpager.setAdapter(new ViewPagerLoginAdapter(this));

        new TabLayoutMediator(loginTab, loginViewpager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Email");
                    } else {
                        tab.setText("Phone");
                    }
                }
        ).attach();

        forgetPasswordVerifyNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this, "Number is Verifying.Please Wait.", Toast.LENGTH_SHORT).show();
                loading_overlay.setVisibility(View.VISIBLE);
                forgetPasswordContainer.setVisibility(View.GONE);
                commonUtilities.closeKeyboard(Login.this, forgetPhoneNumber);
                verifyNumber();
            }
        });

        forgetPasswordVerifyOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading_overlay.setVisibility(View.VISIBLE);
                forgetPasswordContainer.setVisibility(View.GONE);
                commonUtilities.closeKeyboard(Login.this, forgetPasswordOTP);
                forgetVerifyOTP(forgetPasswordOTP.getText().toString());
            }
        });

        forgetPasswordNewPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userNewPassword = forgetPasswordNewPassword.getText().toString().trim();
                String userConfirmPassword = forgetPasswordConfirmNewPassword.getText().toString().trim();

                if (!userNewPassword.equals(userConfirmPassword)) {
                    forgetPasswordNewPassword.setError("Password did not match");
                    forgetPasswordConfirmNewPassword.setError("Password did not match");
                } else {
                    commonUtilities.closeKeyboard(Login.this, forgetPasswordConfirmNewPassword);
                    loading_overlay.setVisibility(View.VISIBLE);
                    forgetPasswordContainer.setVisibility(View.GONE);
                    changePassword();
                }
            }
        });

        forgetPasswordBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isForgettingPassword = false;
                forgetPasswordContainer.setVisibility(View.GONE);
                login_page.setVisibility(View.VISIBLE);
            }
        });

        otpBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginOTPContainer.setVisibility(View.GONE);
                login_page.setVisibility(View.VISIBLE);
            }
        });

        loginOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loading_overlay.setVisibility(View.VISIBLE);
                loginOTPContainer.setVisibility(View.GONE);

                commonUtilities.closeKeyboard(Login.this, loginOTP);
                verifyOTP(loginOTP.getText().toString());
            }
        });

        loginBuyConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Registration.class));
            }
        });

        loginForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonUtilities.clearEditText(forgetPhoneNumber);
                commonUtilities.clearEditText(forgetPasswordOTP);
                commonUtilities.clearEditText(forgetPasswordNewPassword);
                commonUtilities.clearEditText(forgetPasswordConfirmNewPassword);

                forgetPasswordPhoneNumber.setVisibility(View.VISIBLE);
                forgetPasswordOTPVerifyContainer.setVisibility(View.GONE);
                forgetPassword_changePasswordContainer.setVisibility(View.GONE);

                isForgettingPassword = true;
                forgetPasswordContainer.setVisibility(View.VISIBLE);
                login_page.setVisibility(View.GONE);
            }
        });

//        back pressed Navigation
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                if (forgetPasswordPhoneNumber.getVisibility() == View.VISIBLE) {
                    forgetPasswordPhoneNumber.setVisibility(View.GONE);
                    login_page.setVisibility(View.VISIBLE);

                } else if (forgetPasswordOTPVerifyContainer.getVisibility() == View.VISIBLE) {
                    forgetPasswordOTPVerifyContainer.setVisibility(View.GONE);
                    forgetPasswordPhoneNumber.setVisibility(View.VISIBLE);

                } else if (forgetPassword_changePasswordContainer.getVisibility() == View.VISIBLE) {
                    forgetPassword_changePasswordContainer.setVisibility(View.GONE);
                    forgetPasswordOTPVerifyContainer.setVisibility(View.VISIBLE);

                } else if (loginOTPContainer.getVisibility() == View.VISIBLE) {
                    loginOTPContainer.setVisibility(View.GONE);
                    login_page.setVisibility(View.VISIBLE);

                } else {
                    finish();
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Check if user is in forget password flow and sign them out
        if (isForgettingPassword == true) {
            FirebaseAuth.getInstance().signOut();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Check if user is in forget password flow and sign them out
        if (isForgettingPassword == true) {
            FirebaseAuth.getInstance().signOut();
        }
    }

    @Override
    public void onDataPass(String phoneNumber) {
        Toast.makeText(this, "Verifying Number, Please Wait", Toast.LENGTH_SHORT).show();
        login_page.setVisibility(View.GONE);
        loading_overlay.setVisibility(View.VISIBLE);
        loginNumber("+91" + phoneNumber);
    }

    @Override
    public void onEmailDataPass(String userEmail, String userPassword) {
        loginEmail(userEmail, userPassword);
    }


    public void showMessage(String message, String title) {
        CookieBar.build(Login.this).setTitle(title).setMessage(message).setCookiePosition(CookieBar.TOP).setDuration(4000).show();
    }

    public void loginNumber(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // (optional) Activity for callback binding
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
//                        Log.d("TAG", "onVerificationCompleted:" + credential);
//                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.w("TAG", "onVerificationFailed", e);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Toast.makeText(Login.this, "Invalid phone number format.", Toast.LENGTH_SHORT).show();
                            loading_overlay.setVisibility(View.GONE);
                            login_page.setVisibility(View.VISIBLE);
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            Toast.makeText(Login.this, "SMS quota exceeded.", Toast.LENGTH_SHORT).show();
                            loading_overlay.setVisibility(View.GONE);
                            login_page.setVisibility(View.VISIBLE);
                        } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                            // reCAPTCHA verification attempted with null Activity
                            Toast.makeText(Login.this, "reCAPTCHA verification failed.", Toast.LENGTH_SHORT).show();
                            loading_overlay.setVisibility(View.GONE);
                            login_page.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        showMessage("OTP has been sent. Please wait!", "");
                        login_page.setVisibility(View.GONE);
                        loginOTPContainer.setVisibility(View.VISIBLE);
                        loading_overlay.setVisibility(View.GONE);

//                        startCounting();

                        mVerificationId = verificationId; // Save verification ID
                        mResendToken = token; // Save resending token
                    }
                })
                .build(); // Corrected here
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    public void verifyOTP(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information

                    FirebaseUser user = task.getResult().getUser();

                    startActivity(new Intent(Login.this, MainActivity.class));
                    finish();
                    // Update UI
                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        showMessage("", "You Entered Wrong OTP");
                        loading_overlay.setVisibility(View.GONE);
                        loginOTPContainer.setVisibility(View.VISIBLE);
                        loginOTP.setError("Please Enter Valid OTP");

                    }
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

            String userOTP = loginOTP.getText().toString().trim();
            String userForgetPhoneNumber = forgetPhoneNumber.getText().toString().trim();
            String userForgetOtp = forgetPasswordOTP.getText().toString().trim();
            String userNewPassword = forgetPasswordNewPassword.getText().toString().trim();
            String userNewConfirmPassword = forgetPasswordConfirmNewPassword.getText().toString().trim();

//            login verify OTP
            loginOTPButton.setEnabled(!TextUtils.isEmpty(userOTP) && userOTP.length() == 6);

//            forget Container Buttons
            forgetPasswordVerifyNumberButton.setEnabled(!TextUtils.isEmpty(userForgetPhoneNumber) && userForgetPhoneNumber.length() == 10);
            forgetPasswordVerifyOTPButton.setEnabled(!TextUtils.isEmpty(userForgetOtp) && userForgetOtp.length() == 6);

            forgetPasswordNewPasswordButton.setEnabled(!TextUtils.isEmpty(userNewPassword) && !TextUtils.isEmpty(userNewConfirmPassword));

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void loginEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                            Toast.makeText(Login.this, "logged in", Toast.LENGTH_SHORT).show();
                            isForgettingPassword = false;
                            startActivity(new Intent(Login.this, MainActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            showMessage("You are either not registered or not approved by Administration !", "Invalid Email and Password");
//                            updateUI(null);
                        }
                    }
                });

    }


    //    Forget password
    public void verifyNumber() {
        String userPhone = "+91" + forgetPhoneNumber.getText().toString();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(userPhone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    // Invalid request

                                    Toast.makeText(Login.this, "Invalid Number. Enter Registered Number", Toast.LENGTH_SHORT).show();
                                    loading_overlay.setVisibility(View.GONE);
                                    forgetPasswordContainer.setVisibility(View.VISIBLE);

                                } else if (e instanceof FirebaseTooManyRequestsException) {
                                    // The SMS quota for the project has been exceeded
                                    Toast.makeText(Login.this, "Requested Too Many Times. Try after sometime.", Toast.LENGTH_SHORT).show();
                                    loading_overlay.setVisibility(View.GONE);
                                    forgetPasswordContainer.setVisibility(View.VISIBLE);
                                } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                                    // reCAPTCHA verification attempted with null Activity
                                    loading_overlay.setVisibility(View.GONE);
                                    forgetPasswordContainer.setVisibility(View.VISIBLE);
                                }

                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                                showMessage("Number Verified. Please Wait for OTP", "");
                                loading_overlay.setVisibility(View.GONE);
                                forgetPasswordContainer.setVisibility(View.VISIBLE);
                                forgetPasswordPhoneNumber.setVisibility(View.GONE);
                                forgetPasswordOTPVerifyContainer.setVisibility(View.VISIBLE);

                                // Save verification ID and resending token so we can use them later
                                mVerificationId = verificationId;
                                mResendToken = token;
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void forgetVerifyOTP(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        forgetSignInWithPhoneAuthCredential(credential);
    }

    private void forgetSignInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = task.getResult().getUser();

                            showMessage("Your Number is Verified. Now Enter New Password", "");
                            loading_overlay.setVisibility(View.GONE);
                            forgetPasswordContainer.setVisibility(View.VISIBLE);
                            forgetPasswordOTPVerifyContainer.setVisibility(View.GONE);
                            forgetPassword_changePasswordContainer.setVisibility(View.VISIBLE);
                            FirebaseAuth.getInstance().signOut();

                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                showMessage("", "Please Enter Valid OTP");
                                loading_overlay.setVisibility(View.GONE);
                                forgetPasswordContainer.setVisibility(View.VISIBLE);
                                forgetPasswordOTPVerifyContainer.setVisibility(View.VISIBLE);

                                forgetPasswordVerifyOTPButton.setError("Please Enter Valid OTP");

                            }
                        }
                    }
                });
    }

    public void changePassword() {
        FirebaseUser userForget = FirebaseAuth.getInstance().getCurrentUser();
        String userNewPassword = forgetPasswordNewPassword.getText().toString();

        userForget.updatePassword(userNewPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showMessage("", "Password Changed Successfully 🎉");
                            isForgettingPassword = false;
                            loading_overlay.setVisibility(View.GONE);
                            forgetPasswordContainer.setVisibility(View.VISIBLE);
                            forgetPasswordContainer.setVisibility(View.GONE);
                            login_page.setVisibility(View.VISIBLE);

                        } else {
                            // Handle failure case
                            showMessage("Failed to change password. Please try again.", "Error");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Log the exception for debugging purposes
                    Log.e("ChangePassword", "Error updating password: ", e);
                    showMessage("Failed to change password: " + e.getMessage(), "Error");
                });
    }

}