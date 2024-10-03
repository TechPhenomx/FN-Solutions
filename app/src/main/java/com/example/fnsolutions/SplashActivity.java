package com.example.fnsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        new Handler().postDelayed(() -> {
            if (currentUser != null) {
                // User is signed in, retrieve user data and redirect to MainActivity
                getUserData(currentUser);
            } else {
                // User is not signed in, redirect to Login_Registration_Activity
                Intent moveToLogin = new Intent(SplashActivity.this, Login_Registration_Activity.class);
                startActivity(moveToLogin);
                finish();
            } // User is not signed in, redirect to Login_Registration_Activity

//            startActivity(new Intent(SplashActivity.this, Login_Registration_Activity.class));
//            finish();
        }, 4000);
    }

    public void getUserData(FirebaseUser user) {
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Extract the name from the document
                    String name = (String) document.getData().get("name");

                    // Redirect to MainActivity with the user's name
                    Intent moveToMain = new Intent(SplashActivity.this, MainActivity.class);
                    moveToMain.putExtra("USER_NAME", name);
                    startActivity(moveToMain);
                    finish();
                } else {
                    Log.d("TAG", "No such document");
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();

//                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
            } else {
                Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "Sign In Failed. Contact Administration", task.getException());
            }
        });
    }
}
