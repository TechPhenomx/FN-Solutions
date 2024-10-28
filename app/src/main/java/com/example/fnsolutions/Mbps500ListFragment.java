package com.example.fnsolutions;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class Mbps500ListFragment extends Fragment {
    ArrayList<rechargeModal> planList = new ArrayList<>();

    public Mbps500ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mbps500_list, container, false);

        planList.add(new rechargeModal("1 Month", "737.18","500", "899"));
        planList.add(new rechargeModal("3 Months", "2050.00","500", "2500"));
        planList.add(new rechargeModal("3+1 Months", "2378.00","500", "2300"));
        planList.add(new rechargeModal("6 Months", "3362.00","500", "4100"));
        planList.add(new rechargeModal("6+3 Months", "4510.00","500", "5500"));
        planList.add(new rechargeModal("12 Months", "5330.00","500", "6500"));
        planList.add(new rechargeModal("12+3 Months", "10660.00","500", "13000"));

        // Set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.rechargePlanList500mbps); // Ensure you have this ID in your layout
        RecyclerRechargeAdapter adapter = new RecyclerRechargeAdapter(requireActivity(), planList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Use LinearLayoutManager or your preferred layout manager
        recyclerView.setAdapter(adapter);

        return view;
    }
}