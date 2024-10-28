package com.example.fnsolutions;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RechargePlanActivity extends AppCompatActivity {
    private RecyclerView rechargePlanRecyclerView;
    private MaterialToolbar rechargePlanToolbar;
    private TabLayout rechargePlanTab;
    private ViewPager2 rechargeViewPager;
    private FirebaseFirestore db;
    private String json;
    private List<String> idsList = new ArrayList<>();

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

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        MaterialToolbar toolbar = findViewById(R.id.appToolbar);
        rechargePlanTab = findViewById(R.id.rechargePlanTab);
        rechargeViewPager = findViewById(R.id.rechargeViewPager);

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


        rechargeViewPager.setAdapter(new RechargePlanAdapter(this));

        new TabLayoutMediator(rechargePlanTab, rechargeViewPager,
                (tab, position) -> {
                    switch (position){
                        case 0:
                            tab.setText("100 MBps");
                            break;
                        case 1:
                            tab.setText("200 MBps");
                            break;
                        case 2:
                            tab.setText("300 MBps");
                            break;
                        case 3:
                            tab.setText("400 MBps");
                            break;
                        case 4:
                            tab.setText("500 MBps");
                            break;

                    }

                }
        ).attach();

    }

}