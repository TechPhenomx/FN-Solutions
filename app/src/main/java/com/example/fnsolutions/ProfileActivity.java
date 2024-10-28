package com.example.fnsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private MaterialTextView tableUserEmail, tableUserPhone, profileUsername, profileName, profileAddress;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RelativeLayout profilePageActivity, loading_overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.userProfile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        tableUserEmail = findViewById(R.id.tableUserEmail);
        tableUserPhone = findViewById(R.id.tableUserPhone);
        profileUsername = findViewById(R.id.profileUsername);
        profileName = findViewById(R.id.profileName);
        profileAddress = findViewById(R.id.profileAddress);
        profilePageActivity = findViewById(R.id.profilePageActivity);
        loading_overlay = findViewById(R.id.loading_overlay);

        MaterialToolbar toolbar = findViewById(R.id.appToolbar);

        profilePageActivity.setVisibility(View.GONE);

        setSupportActionBar(toolbar);

        // Set a dynamic title
        getSupportActionBar().setTitle("Your Profile");

        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        // Handle back button click
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        getUserData(currentUser);

    }

    public void getUserData(FirebaseUser user) {

        DocumentReference userDatabase = db.collection("user").document(currentUser.getUid());

        userDatabase.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String name = document.getString("name");
                        String username = document.getString("username");
                        String email = document.getString("email");
                        String phone = document.getString("phone");
                        String address = document.getString("address");

                        tableUserEmail.setText(email);
                        tableUserPhone.setText("+91-" + phone);
                        profileUsername.setText(username);
                        profileName.setText(name.substring(0,1).toUpperCase() + name.substring(1));
                        profileAddress.setText(address);

                        loading_overlay.setVisibility(View.GONE);
                        profilePageActivity.setVisibility(View.VISIBLE);

                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }
}