package com.example.fnsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    MaterialCardView profile, rechargePlan, ticketRaise, ticketRaiseHistoryCard, transactionHistoryCard, dataUsageCard;
    private FirebaseFirestore db;
    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    RelativeLayout mainActivityPage, loading_overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        mainActivityPage = findViewById(R.id.mainActivityPage);
        loading_overlay = findViewById(R.id.loading_overlay);

        // Check if the user is logged in
        if (currentUser != null) {
            mainActivityPage.setVisibility(View.GONE);
            getUserData(currentUser);
        }

        // Retrieve the user name from the Intent
        Intent intent = getIntent();
        String userName = intent.getStringExtra("name");

        MaterialToolbar toolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);  // Set the toolbar as the action bar


        // Set the menu
        toolbar.inflateMenu(R.menu.app_toolbar);

        profile = findViewById(R.id.profileCard);
        rechargePlan = findViewById(R.id.rechargePlanCard);
        ticketRaise = findViewById(R.id.ticketRaiseCard);
        ticketRaiseHistoryCard = findViewById(R.id.ticketRaiseHistoryCard);
        transactionHistoryCard = findViewById(R.id.transactionHistoryCard);
        dataUsageCard = findViewById(R.id.dataUsageCard);

        profile.setOnClickListener(view -> profileCardOpen());
        rechargePlan.setOnClickListener(view -> rechargeCardOpen());
        ticketRaise.setOnClickListener(view -> openRaiseTicketActivity());

        dataUsageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, dataUsageActivity.class));
            }
        });

        ticketRaiseHistoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TicketHistoryActivity.class));
            }
        });

        transactionHistoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TransactionHistoryActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loading_overlay.setVisibility(View.GONE);
        mainActivityPage.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_toolbar, menu); // Inflate the menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Handle clicks on the menu item
        if (id == R.id.logOut) {
            showAlertDialog(); // Show alert dialog on log out click
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void profileCardOpen() {
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
    }

    private void rechargeCardOpen() {
        startActivity(new Intent(MainActivity.this, RechargePlanActivity.class));
    }

    private void openRaiseTicketActivity() {
        startActivity(new Intent(MainActivity.this, HelpAndSupportActivity.class));
    }

    private void showAlertDialog() {
        new AlertDialog.Builder(this).setTitle("Log Out").setMessage("Are you sure you want to log out?").setPositiveButton("Yes", (dialog, which) -> {
            // Handle log out action here

            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            // Optionally, navigate to login or main screen
            startActivity(new Intent(MainActivity.this, Login.class));
            finish(); // Finish the current activity
        }).setNegativeButton("No", (dialog, which) -> dialog.dismiss()).show();
    }

    public void getUserData(FirebaseUser user) {

        DocumentReference userDatabase = db.collection("user").document(user.getUid());

        userDatabase.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String userName = document.getString("name");
                        // Set a dynamic title
                        getSupportActionBar().setTitle("Hello " + userName + " 👋");
                        loading_overlay.setVisibility(View.GONE);
                        mainActivityPage.setVisibility(View.VISIBLE);

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
