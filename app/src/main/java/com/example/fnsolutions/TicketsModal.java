package com.example.fnsolutions;

import com.google.firebase.Timestamp;

public class TicketsModal {
    String issueRaised, description, ticketID, ticketStatus;
    Timestamp createDate;
    TicketsModal(String issueRaised, String description, String ticketID, String ticketStatus, Timestamp createDate){
        this.issueRaised = issueRaised;
        this.description = description;
        this.ticketID = ticketID;
        this.ticketStatus = ticketStatus;
        this.createDate = createDate;
    }
}
