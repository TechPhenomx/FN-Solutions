package com.example.fnsolutions;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class Mbps400ListFragment extends Fragment {

    ArrayList<rechargeModal> planList = new ArrayList<>();

    public Mbps400ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mbps400_list, container, false);
        planList.add(new rechargeModal("1 Month", "655.18","400", "799"));
        planList.add(new rechargeModal("3 Months", "1804.00","400", "2200"));
        planList.add(new rechargeModal("3+1 Months", "2091.00","400", "2550"));
        planList.add(new rechargeModal("6 Months", "3034.00","400", "3700"));
        planList.add(new rechargeModal("6+3 Months", "4141.00","400", "5050"));
        planList.add(new rechargeModal("12 Months", "4920.00","400", "6000"));
        planList.add(new rechargeModal("12+3 Months", "8610.00","400", "10500"));


        // Set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.rechargePlanList400mbps); // Ensure you have this ID in your layout
        RecyclerRechargeAdapter adapter = new RecyclerRechargeAdapter(requireActivity(), planList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Use LinearLayoutManager or your preferred layout manager
        recyclerView.setAdapter(adapter);

        return view;
    }
}