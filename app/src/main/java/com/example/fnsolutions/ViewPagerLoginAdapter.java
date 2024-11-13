package com.example.fnsolutions;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerTitleStrip;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerLoginAdapter extends FragmentStateAdapter {


    public ViewPagerLoginAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new emailLoginFragment();
        } else {
            return new phoneLoginFragment();
        }
    }


    @Override
    public int getItemCount() {
        return 2;
    }

    // Method to get tab titles




    
    public CharSequence getTabTitle(int position) {
        if(position == 0){
            return "Username";
        }else{
            return "Phone";
        }
    }

}
