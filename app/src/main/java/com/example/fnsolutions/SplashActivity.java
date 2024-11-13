package com.example.fnsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    FirebaseUser currentUser;

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
        currentUser = mAuth.getCurrentUser();

        new Handler().postDelayed(() -> {
            if (currentUser != null) {
//                getUserData(currentUser);
                Intent moveToMain = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(moveToMain);
                finish();

            } else {
                // User is not signed in, redirect to Login_Registration_Activity
                Intent moveToLogin = new Intent(SplashActivity.this, Login.class);
                startActivity(moveToLogin);
                finish();
            }
        }, 4000);
    }

//    public void getUserData(FirebaseUser user) {
//
//        DocumentReference userDatabase = db.collection("user").document(currentUser.getUid());
//
//        userDatabase.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//
//                        String userName = document.getString("name");
//
//                        Intent moveToMain = new Intent(SplashActivity.this, MainActivity.class);
//                        moveToMain.putExtra("name", userName);
//                        startActivity(moveToMain);
//                        finish();
//
//                    } else {
//                        Log.d("TAG", "No such document");
//                    }
//                } else {
//                    Log.d("TAG", "get failed with ", task.getException());
//                }
//            }
//        });
//    }
}
