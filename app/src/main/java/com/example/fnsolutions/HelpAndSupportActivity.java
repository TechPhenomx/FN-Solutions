package com.example.fnsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HelpAndSupportActivity extends AppCompatActivity {

    private CheckBox noInternetCheckbox, disconnectsFrequentlyCheckbox, cutWireCheckbox, slowSpeedCheckbox, otherCheckbox;
    private EditText issueDescriptionEditText;
    private Button helpAndSupportSubmitButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private RelativeLayout helpAndSupportContainer, loading_overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help_and_support);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set up UI elements
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        noInternetCheckbox = findViewById(R.id.noInternetCheckbox);
        slowSpeedCheckbox = findViewById(R.id.slowSpeedCheckbox);
        otherCheckbox = findViewById(R.id.otherCheckbox);
        issueDescriptionEditText = findViewById(R.id.issueDescriptionEditText);
        helpAndSupportSubmitButton = findViewById(R.id.helpAndSupportSubmitButton);

        helpAndSupportContainer = findViewById(R.id.helpAndSupportContainer);
        loading_overlay = findViewById(R.id.loading_overlay);

        loading_overlay.setVisibility(View.GONE);
        helpAndSupportContainer.setVisibility(View.VISIBLE);

        MaterialToolbar toolbar = findViewById(R.id.appToolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Help And Support");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }


        // Add listeners for each checkbox to monitor changes
        noInternetCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> updateSubmitButtonState());
        slowSpeedCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> updateSubmitButtonState());

        // Add a text watcher to monitor changes in the issue description field
        issueDescriptionEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Re-enable the submit button when description is entered
                updateSubmitButtonState();
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {
            }
        });

        // Add listeners for each checkbox to ensure only one is selected at a time
        noInternetCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                slowSpeedCheckbox.setChecked(false);
                otherCheckbox.setChecked(false);
            }
            updateSubmitButtonState();
        });

        slowSpeedCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                noInternetCheckbox.setChecked(false);
                otherCheckbox.setChecked(false);
            }
            updateSubmitButtonState();
        });

        otherCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                noInternetCheckbox.setChecked(false);
                slowSpeedCheckbox.setChecked(false);
                issueDescriptionEditText.setVisibility(View.VISIBLE);
            }else{
                issueDescriptionEditText.setVisibility(View.GONE);
            }

            updateSubmitButtonState();
        });

//        helpAndSupportSubmitButton.setOnClickListener(v -> submitIssue());

        helpAndSupportSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading_overlay.setVisibility(View.VISIBLE);
                helpAndSupportContainer.setVisibility(View.GONE);
                submitIssue();
            }
        });

    }

    // Method to check if any checkbox is selected and enable the submit button accordingly
    private void updateSubmitButtonState() {
        boolean isAnyCheckboxChecked = noInternetCheckbox.isChecked() || slowSpeedCheckbox.isChecked() || otherCheckbox.isChecked();
        String description = issueDescriptionEditText.getText().toString();
        boolean isDescriptionEntered = !description.isEmpty();
        helpAndSupportSubmitButton.setEnabled(isAnyCheckboxChecked || isDescriptionEntered);
    }

    private void submitIssue() {
        // Create the issue data map
//        Map<String, Object> issueData = new HashMap<>();
        Map<String, Object> ticketData = new HashMap<>();
        Map<String, Object> ticketMap = new HashMap<>();
        String issue = "";

        // Add issue checkboxes
        if (noInternetCheckbox.isChecked()) {
//            issueData.put("No Internet Connection", true);
            issue = "No Internet Connection";
        }

        if (slowSpeedCheckbox.isChecked()) {
//            issueData.put("Internet Speed is Slow", true);
            issue = "Slow Internet Speed";
        }
        if (otherCheckbox.isChecked()) {
//            issueData.put("Other", true);
            issue = "Other";
        }

        // Get the issue description, and set a default value if empty
        String description = issueDescriptionEditText.getText().toString();
        if (description.isEmpty()) {
            description = "No description provided";  // Default description
        }

        ticketMap.put("Description", description);

        String userId = mAuth.getCurrentUser().getUid();

        DocumentReference userDocRef = db.collection("users").document(userId);

        DocumentReference userTicketDoc = db.collection("tickets").document(userId);

        // Add the issue data
//        ticketMap.put("issue", issueData);

        // Add additional fields to the ticket data
        ticketMap.put("createDate", Timestamp.now());  // Current timestamp
        ticketMap.put("ticketID", UUID.randomUUID().toString());  // Random ticket ID
        ticketMap.put("ticketStatus", "pending");  // Static ticket status as "pending"
        ticketMap.put("updateDate", Timestamp.now());  // Current timestamp
        ticketMap.put("issue", issue);

        // Add the ticket map to the ticketArray
        ticketData.put("ticketArray", FieldValue.arrayUnion(ticketMap));

        // Add the user data reference to the ticket document
        ticketData.put("userData", userDocRef);  // Add a reference to the user document

        // Now check if the document already exists for the user and update the ticket array
        userTicketDoc.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // If the document exists, update the ticketArray
                db.collection("tickets").document(userId).update("ticketArray", FieldValue.arrayUnion(ticketMap)).addOnSuccessListener(unused -> {
                    Toast.makeText(HelpAndSupportActivity.this, "Ticket submitted successfully!", Toast.LENGTH_SHORT).show();

                }).addOnFailureListener(e -> {
                    Log.e("TAG", "Error updating ticket array: " + e.getMessage(), e);
                    Toast.makeText(HelpAndSupportActivity.this, "Error submitting ticket: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                });

                startActivity(new Intent(HelpAndSupportActivity.this, MainActivity.class));
                finish();
            } else {
                // If the document doesn't exist, create it from scratch
                ticketData.put("ticketArray", FieldValue.arrayUnion(ticketMap));
                ticketData.put("userData", userDocRef);  // Add a reference to the user document

                // Create a new document in Firestore
                userTicketDoc.set(ticketData).addOnSuccessListener(unused -> {
                    Toast.makeText(HelpAndSupportActivity.this, "Ticket submitted successfully!", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Log.e("TAG", "Error creating ticket document: " + e.getMessage(), e);
                    Toast.makeText(HelpAndSupportActivity.this, "Error submitting ticket: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

                startActivity(new Intent(HelpAndSupportActivity.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(e -> {
            Log.e("TAG", "Error checking ticket document: " + e.getMessage(), e);
            Toast.makeText(HelpAndSupportActivity.this, "Error checking existing tickets: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }


}
