package com.example.fnsolutions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RecyclerTicketsAdapter extends RecyclerView.Adapter<RecyclerTicketsAdapter.ViewHolder>{

    Context context;
    ArrayList<TicketsModal> ticketListArray;

    RecyclerTicketsAdapter(Context context, ArrayList<TicketsModal> ticketListArray){
        this.context = context;
        this.ticketListArray = ticketListArray;
    }


    @NonNull
    @Override
    public RecyclerTicketsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ticket_history_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerTicketsAdapter.ViewHolder holder, int position) {
        TicketsModal currentUser = ticketListArray.get(position);
        holder.userIssueRaised.setText(currentUser.issueRaised);
        holder.userDescription.setText(currentUser.description);
        holder.userTicketID.setText(currentUser.ticketID);
        holder.userTicketStatus.setText(currentUser.ticketStatus);

        // Get the Timestamp from the TicketsModal object
        Timestamp createDate = currentUser.createDate;

        if (createDate != null) {
            // Convert Timestamp to Date
            Date date = createDate.toDate();

            // Format the Date to display in a specific format
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            String formattedDate = sdf.format(date);

            // Set the formatted date to a TextView or log it
            holder.createDate.setText(formattedDate); // Assuming you have a TextView to display this
        } else {
            // If createDate is null, display a default message
            holder.createDate.setText("Date not available");
        }
    }

    @Override
    public int getItemCount() {
        return ticketListArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userIssueRaised, userDescription, userTicketID, userTicketStatus, createDate;
        LinearLayout ticketCardLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userIssueRaised = itemView.findViewById(R.id.issueRaised);
            userDescription = itemView.findViewById(R.id.description);
            userTicketID = itemView.findViewById(R.id.ticketID);
            userTicketStatus = itemView.findViewById(R.id.ticketStatus);
            createDate = itemView.findViewById(R.id.createDate);
//            ticketCardLayout = itemView.findViewById(R.id.ticketCardLayout);
        }
    }
}
