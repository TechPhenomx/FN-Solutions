package com.example.fnsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    MaterialCardView profile, rechargePlan, ticketRaise;

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

        // Retrieve the user name from the Intent
        Intent intent = getIntent();
        String userName = intent.getStringExtra("USER_NAME");

        MaterialToolbar toolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);  // Set the toolbar as the action bar

        // Set a dynamic title
        toolbar.setTitle("Hello " + userName + " 👋");

        // Set the menu
        toolbar.inflateMenu(R.menu.app_toolbar);

        profile = findViewById(R.id.profileCard);
        rechargePlan = findViewById(R.id.rechargePlanCard);
        ticketRaise = findViewById(R.id.ticketRaiseCard);

        profile.setOnClickListener(view -> profileCardOpen());
        rechargePlan.setOnClickListener(view -> rechargeCardOpen());
        ticketRaise.setOnClickListener(view -> openRaiseTicketActivity());
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
            startActivity(new Intent(MainActivity.this, Login_Registration_Activity.class));
            finish(); // Finish the current activity
        }).setNegativeButton("No", (dialog, which) -> dialog.dismiss()).show();
    }
}
