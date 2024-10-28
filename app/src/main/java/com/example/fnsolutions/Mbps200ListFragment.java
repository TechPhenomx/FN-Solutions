package com.example.fnsolutions;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class Mbps200ListFragment extends Fragment {

    ArrayList<rechargeModal> planList = new ArrayList<>();

    public Mbps200ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mbps200_list, container, false);

        planList.add(new rechargeModal("1 Month", "491.18","200", "599"));
        planList.add(new rechargeModal("3 Months", "1312.00","200", "1600"));
        planList.add(new rechargeModal("3+1 Months", "1517.00","200", "1850"));
        planList.add(new rechargeModal("6 Months", "2378.00","200", "2900"));
        planList.add(new rechargeModal("6+3 Months", "3403.00","200", "4150"));
        planList.add(new rechargeModal("12 Months", "4100.00","200", "5000"));
        planList.add(new rechargeModal("12+3 Months", "5740.00","200", "7000"));


        // Set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.rechargePlanList200mbps); // Ensure you have this ID in your layout
        RecyclerRechargeAdapter adapter = new RecyclerRechargeAdapter(requireActivity(), planList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Use LinearLayoutManager or your preferred layout manager
        recyclerView.setAdapter(adapter);

        return view;
    }
}