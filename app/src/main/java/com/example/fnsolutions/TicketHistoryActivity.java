package com.example.fnsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TicketHistoryActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ArrayList<TicketsModal> raiseTicketList = new ArrayList<>();
    private RecyclerView ticketHistoryList;
    private RelativeLayout ticketHistoryContainer, loading_overlay;
    private LinearLayout ticketHistoryNoDataFoundWrap, ticketHistoryWrap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ticket_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ticketHistoryList = findViewById(R.id.ticketHistoryList);
        ticketHistoryContainer = findViewById(R.id.ticketHistoryContainer);
        loading_overlay = findViewById(R.id.loading_overlay);
        ticketHistoryNoDataFoundWrap = findViewById(R.id.ticketHistoryNoDataFoundWrap);
        ticketHistoryWrap = findViewById(R.id.ticketHistoryWrap);

        loading_overlay.setVisibility(View.VISIBLE);
        ticketHistoryContainer.setVisibility(View.GONE);

        MaterialToolbar toolbar = findViewById(R.id.appToolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Ticket History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        checkTicketHistoryExist();

    }

    private void checkTicketHistoryExist() {

        db.collection("tickets")
                .document(mAuth.getCurrentUser().getUid())  // Check by document ID
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Get the ticketArray from the document (assumes the field name is 'ticketArray')
                            List<Map<String, Object>> ticketArray = (List<Map<String, Object>>) document.get("ticketArray");
                            if (!ticketArray.isEmpty()) {
                                // Loop through the array
                                for (Map<String, Object> ticket : ticketArray) {
                                    // Access the fields of each ticket
                                    String issue = (String) ticket.get("issue");
                                    String ticketId = (String) ticket.get("ticketID");
                                    String description = (String) ticket.get("Description");
                                    Timestamp createDate = (Timestamp) ticket.get("createDate");
                                    String ticketStatus = (String) ticket.get("ticketStatus");

                                    Log.d("TicketHistory", "Ticket ID: " + ticketId + ", Issue: " + issue);

                                    // Optionally, you can show these details in your UI
                                    raiseTicketList.add(new TicketsModal(issue, description, ticketId, ticketStatus, createDate));
                                }

                                loading_overlay.setVisibility(View.GONE);
                                ticketHistoryContainer.setVisibility(View.VISIBLE);
                                ticketHistoryNoDataFoundWrap.setVisibility(View.GONE);
                                ticketHistoryWrap.setVisibility(View.VISIBLE);

                                RecyclerTicketsAdapter adapter = new RecyclerTicketsAdapter(this, raiseTicketList);
                                ticketHistoryList.setLayoutManager(new LinearLayoutManager(this));
                                ticketHistoryList.setAdapter(adapter);
                            } else {
                                Toast.makeText(TicketHistoryActivity.this, "No tickets found!", Toast.LENGTH_SHORT).show();
                                loading_overlay.setVisibility(View.GONE);
                                ticketHistoryContainer.setVisibility(View.VISIBLE);
                                ticketHistoryNoDataFoundWrap.setVisibility(View.VISIBLE);
                                ticketHistoryWrap.setVisibility(View.GONE);
                            }

                        } else {
                            // User does not exist
                            Toast.makeText(TicketHistoryActivity.this, "User does not exist!", Toast.LENGTH_SHORT).show();
                            loading_overlay.setVisibility(View.GONE);

                            startActivity(new Intent(TicketHistoryActivity.this, MainActivity.class));
                            finish();
                        }
                    } else {
                        // Error getting document
                        Toast.makeText(TicketHistoryActivity.this, "Error checking user", Toast.LENGTH_SHORT).show();
                        loading_overlay.setVisibility(View.GONE);

                        startActivity(new Intent(TicketHistoryActivity.this, MainActivity.class));
                        finish();
                    }
                });
    }
}