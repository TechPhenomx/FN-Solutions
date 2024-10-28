package com.example.fnsolutions;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class Mbps100ListFragment extends Fragment {

    ArrayList<rechargeModal> planList = new ArrayList<>();

    public Mbps100ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_mbps100_list, container, false);

        planList.add(new rechargeModal("1 Month", "409.18","100", "499"));
        planList.add(new rechargeModal("3 Month", "1060.00","100", "1300"));
        planList.add(new rechargeModal("3+1 Month", "1230.00","100", "1500"));
        planList.add(new rechargeModal("6 Month", "2050.00","100", "2500"));
        planList.add(new rechargeModal("6+3 Month", "3034.00","100", "3700"));
        planList.add(new rechargeModal("12 Month", "3690.00","100", "4500"));
        planList.add(new rechargeModal("12+3 Month", "4920.00","100", "6000"));

        // Set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.rechargePlanList100mbps); // Ensure you have this ID in your layout
        RecyclerRechargeAdapter adapter = new RecyclerRechargeAdapter(requireActivity(), planList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Use LinearLayoutManager or your preferred layout manager
        recyclerView.setAdapter(adapter);

        return view;
    }
}