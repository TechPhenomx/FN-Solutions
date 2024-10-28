package com.example.fnsolutions;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class RechargePlanAdapter extends FragmentStateAdapter {


    public RechargePlanAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Create fragment based on the position
        switch (position) {
            case 0:
                return new Mbps100ListFragment();
            case 1:
                return new Mbps200ListFragment();
            case 2:
                return new Mbps300ListFragment();
            case 3:
                return new Mbps400ListFragment();
            case 4:
                return new Mbps500ListFragment();

        }

        return null;
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    // Method to get tab titles
    public CharSequence getTabTitle(int position) {

        switch (position){
            case 0:
                return "100 MBps";
            case 1:
                return "200 MBps";
            case 2:
                return "300 MBps";
            case 3:
                return "400 MBps";
            case 4:
                return "500 MBps";

        }
        return null;
    }


}
