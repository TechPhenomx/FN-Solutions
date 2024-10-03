package com.example.fnsolutions;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class RechargePlanActivity extends AppCompatActivity {
    private RecyclerView rechargePlanRecyclerView;
    private ArrayList<rechargeModal> rechargePlan = new ArrayList<>();
    private MaterialToolbar rechargePlanToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recharge_plan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);

        // Set a dynamic title
        getSupportActionBar().setTitle("Your Plan");

        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        // Handle back button click
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        // Set up RecyclerView
        setupRecyclerView();
    }

//    private void setupToolbar(){
//        setSupportActionBar(rechargePlanToolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//            getSupportActionBar().setTitle("Hello User");
//        }
//    }

    private void setupRecyclerView() {
        rechargePlanRecyclerView = findViewById(R.id.rechargePlanList);
        rechargePlanRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        populateRechargePlan();
        RecyclerRechargeAdapter adapter = new RecyclerRechargeAdapter(this, rechargePlan);
        rechargePlanRecyclerView.setAdapter(adapter);
    }

    private void populateRechargePlan() {
        // Adding data to the rechargePlan ArrayList
        //100 MBPS
        rechargePlan.add(new rechargeModal("1 Month", "499", "100 Mbps"));
        rechargePlan.add(new rechargeModal("3 Months", "1300", "100 Mbps"));
        rechargePlan.add(new rechargeModal("3+1 Months", "1500", "100 Mbps"));
        rechargePlan.add(new rechargeModal("6 Months", "2500", "100 Mbps"));
        rechargePlan.add(new rechargeModal("6+3 Months", "3780", "100 Mbps"));
        rechargePlan.add(new rechargeModal("12 Months", "4500", "100 Mbps"));
        rechargePlan.add(new rechargeModal("12+3 Months", "6000", "100 Mbps"));

        //200 MBPS
        rechargePlan.add(new rechargeModal("1 Month", "599", "200 Mbps"));
        rechargePlan.add(new rechargeModal("3 Months", "1600", "200 Mbps"));
        rechargePlan.add(new rechargeModal("3+1 Months", "1850", "200 Mbps"));
        rechargePlan.add(new rechargeModal("6 Months", "2900", "200 Mbps"));
        rechargePlan.add(new rechargeModal("6+3 Months", "4150", "200 Mbps"));
        rechargePlan.add(new rechargeModal("12 Months", "5000", "200 Mbps"));
        rechargePlan.add(new rechargeModal("12+3 Months", "7000", "200 Mbps"));

        //300 MBPS
        rechargePlan.add(new rechargeModal("1 Month", "649", "300 Mbps"));
        rechargePlan.add(new rechargeModal("3 Months", "1900", "300 Mbps"));
        rechargePlan.add(new rechargeModal("3+1 Months", "2200", "300 Mbps"));
        rechargePlan.add(new rechargeModal("6 Months", "3300", "300 Mbps"));
        rechargePlan.add(new rechargeModal("6+3 Months", "4600", "300 Mbps"));
        rechargePlan.add(new rechargeModal("12 Months", "5500", "300 Mbps"));
        rechargePlan.add(new rechargeModal("12+3 Months", "8500", "300 Mbps"));

        //400 MBPS
        rechargePlan.add(new rechargeModal("1 Month", "799", "400 Mbps"));
        rechargePlan.add(new rechargeModal("3 Months", "2200", "400 Mbps"));
        rechargePlan.add(new rechargeModal("3+1 Months", "2550", "400 Mbps"));
        rechargePlan.add(new rechargeModal("3 Months", "3700", "400 Mbps"));
        rechargePlan.add(new rechargeModal("6+3 Months", "5050", "400 Mbps"));
        rechargePlan.add(new rechargeModal("12 Months", "6000", "400 Mbps"));
        rechargePlan.add(new rechargeModal("12+3 Months", "10500", "400 Mbps"));

        //500 MBPS
        rechargePlan.add(new rechargeModal("1 Month", "899", "500 Mbps"));
        rechargePlan.add(new rechargeModal("3 Months", "2500", "500 Mbps"));
        rechargePlan.add(new rechargeModal("3+1 Months", "2900", "500 Mbps"));
        rechargePlan.add(new rechargeModal("6 Months", "4100", "500 Mbps"));
        rechargePlan.add(new rechargeModal("6+3 Months", "5500", "500 Mbps"));
        rechargePlan.add(new rechargeModal("12 Months", "6500", "500 Mbps"));
        rechargePlan.add(new rechargeModal("12+3 Months", "13000", "500 Mbps"));


    }


}