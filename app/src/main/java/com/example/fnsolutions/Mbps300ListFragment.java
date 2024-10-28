package com.example.fnsolutions;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class Mbps300ListFragment extends Fragment {
    ArrayList<rechargeModal> planList = new ArrayList<>();

    public Mbps300ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mbps300_list, container, false);

        planList.add(new rechargeModal("1 Month", "532.18","300", "649"));
        planList.add(new rechargeModal("3 Months", "1558.00","300", "1900"));
        planList.add(new rechargeModal("3+1 Months", "1804.00","300", "2200"));
        planList.add(new rechargeModal("6 Months", "2706.00","300", "3300"));
        planList.add(new rechargeModal("6+3 Months", "3772.00","300", "4600"));
        planList.add(new rechargeModal("12 Months", "4510.00","300", "5500"));
        planList.add(new rechargeModal("12+3 Months", "6970.00","300", "8500"));


        // Set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.rechargePlanList300mbps); // Ensure you have this ID in your layout
        RecyclerRechargeAdapter adapter = new RecyclerRechargeAdapter(requireActivity(), planList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Use LinearLayoutManager or your preferred layout manager
        recyclerView.setAdapter(adapter);

        return view;
    }
}