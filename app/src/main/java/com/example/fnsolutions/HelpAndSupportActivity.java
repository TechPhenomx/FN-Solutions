package com.example.fnsolutions;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpAndSupportActivity extends AppCompatActivity {

    private CheckBox noInternetCheckbox, disconnectsFrequentlyCheckbox, cutWireCheckbox, slowSpeedCheckbox, otherCheckbox;
    private EditText issueDescriptionEditText;
    private Button helpAndSupportSubmitButton;
    private FirebaseFirestore db;

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


            MaterialToolbar toolbar = findViewById(R.id.appToolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle("Help And Support");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                toolbar.setNavigationOnClickListener(v -> onBackPressed());
            }

            noInternetCheckbox = findViewById(R.id.noInternetCheckbox);
            disconnectsFrequentlyCheckbox = findViewById(R.id.disconnectsFrequentlyCheckbox);
            cutWireCheckbox = findViewById(R.id.cutWireCheckbox);
            slowSpeedCheckbox = findViewById(R.id.slowSpeedCheckbox);
            otherCheckbox = findViewById(R.id.otherCheckbox);
            issueDescriptionEditText = findViewById(R.id.issueDescriptionEditText);
            helpAndSupportSubmitButton = findViewById(R.id.helpAndSupportSubmitButton);

            helpAndSupportSubmitButton.setOnClickListener(v -> submitIssue());

            // Prepare and store recharge plans
//            setupRechargePlans();

            Log.d("HelpAndSupportActivity", "Views initialized successfully");

        // Fetch recharge plans
        fetchAllRechargePlansAsJson();


    }

    private void fetchAllRechargePlansAsJson() {
        db.collection("rechargePlans")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> plansList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> planData = new HashMap<>();
                            planData.put("id", document.getId()); // Get the document ID
                            planData.putAll(document.getData());  // Get the document data
                            plansList.add(planData);
                        }

                        // Convert the list to JSON
                        String json = new Gson().toJson(plansList);
                        Log.d("JSON Data", json);

                        // Optionally handle the JSON string, like sending it to another function
                        handleJsonData(json);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                        Toast.makeText(this, "Error retrieving data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleJsonData(String json) {
        // Process the JSON string as needed
        Log.d("JSON Data", "Received JSON: " + json);
    }


//    private void handleRechargePlans(List<Map<String, Object>> plansList) {
//        // Process the list as needed, e.g., display in a RecyclerView or ListView
//        for (Map<String, Object> plan : plansList) {
//            Log.d("Plan", plan.toString());
//        }
//    }


//    private void setupRechargePlans() {
//        Map<String, Object> rechargePlans = new HashMap<>();
//
//        rechargePlans.put("100", createPlanList(new String[][]{
//                {"1 Month", "499"},
//                {"3 Months", "1300"},
//                {"3+1 Months", "1500"},
//                {"6 Months", "2500"},
//                {"6+3 Months", "3700"},
//                {"12 Months", "4500"},
//                {"12+3 Months", "6000"},
//        }));
//
//        rechargePlans.put("200", createPlanList(new String[][]{
//                {"1 Month", "599"},
//                {"3 Months", "1600"},
//                {"3+1 Months", "1850"},
//                {"6 Months", "2900"},
//                {"6+3 Months", "4150"},
//                {"12 Months", "5000"},
//                {"12+3 Months", "7000"},
//        }));
//
//        rechargePlans.put("300", createPlanList(new String[][]{
//                {"1 Month", "649"},
//                {"3 Months", "1900"},
//                {"3+1 Months", "2200"},
//                {"6 Months", "3300"},
//                {"6+3 Months", "4600"},
//                {"12 Months", "5500"},
//                {"12+3 Months", "8500"},
//        }));
//
//        rechargePlans.put("400", createPlanList(new String[][]{
//                {"1 Month", "799"},
//                {"3 Months", "2200"},
//                {"3+1 Months", "2550"},
//                {"6 Months", "3700"},
//                {"6+3 Months", "5050"},
//                {"12 Months", "6000"},
//                {"12+3 Months", "10500"},
//        }));
//
//        rechargePlans.put("500", createPlanList(new String[][]{
//                {"1 Month", "899"},
//                {"3 Months", "2500"},
//                {"3+1 Months", "2900"},
//                {"6 Months", "4100"},
//                {"6+3 Months", "5500"},
//                {"12 Months", "6500"},
//                {"12+3 Months", "13000"},
//        }));
//
//        // Store in Firestore
//        for (String key : rechargePlans.keySet()) {
//            db.collection("rechargePlans")
//                    .document(key)
//                    .set((Map<String, Object>) rechargePlans.get(key), SetOptions.merge())
//                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document " + key + " successfully written!"))
//                    .addOnFailureListener(e -> {
//                        Log.w("Firestore", "Error writing document", e);
//                        Toast.makeText(this, "Error writing document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    });
//        }
//    }


//    private Map<String, Object> createPlanList(String[][] plans) {
//        Map<String, Object> planMap = new HashMap<>();
//        for (String[] plan : plans) {
//            planMap.put(plan[0], plan[1]);  // plan[0] is the time duration, plan[1] is the cost
//        }
//        return planMap;
//    }



    private void submitIssue() {
        Map<String, Object> issueData = new HashMap<>();
        if (noInternetCheckbox.isChecked()) {
            issueData.put("No Internet Connection", true);
        }
        if (disconnectsFrequentlyCheckbox.isChecked()) {
            issueData.put("Wi-Fi Signal Disconnects Frequently", true);
        }
        if (cutWireCheckbox.isChecked()) {
            issueData.put("Cut Wire", true);
        }
        if (slowSpeedCheckbox.isChecked()) {
            issueData.put("Internet Speed is Slow", true);
        }
        if (otherCheckbox.isChecked()) {
            issueData.put("Other", true);
        }

        String description = issueDescriptionEditText.getText().toString();
        if (!description.isEmpty()) {
            issueData.put("Description", description);
        }

        Log.d("IssueData", issueData.toString());

        db.collection("ticketRaised").document("user").set(issueData, SetOptions.merge())
                .addOnSuccessListener(unused -> {
                    Toast.makeText(HelpAndSupportActivity.this, "DocumentSnapshot successfully written!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w("TAG", "Error writing document", e);
                    Toast.makeText(HelpAndSupportActivity.this, "Error writing document", Toast.LENGTH_SHORT).show();
                });
    }
}
