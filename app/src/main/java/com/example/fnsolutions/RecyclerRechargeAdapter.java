package com.example.fnsolutions;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerRechargeAdapter extends RecyclerView.Adapter<RecyclerRechargeAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<rechargeModal> internetPlan;


    public RecyclerRechargeAdapter(Context context, ArrayList<rechargeModal> internetPlan){
        this.context = context;
        this.internetPlan = internetPlan;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recharge_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        rechargeModal plan = internetPlan.get(position);
        holder.internetSpeed.setText(plan.speedMBps);
        holder.timePeriod.setText(plan.timePeriod);
        holder.rechargePlan.setText(plan.planPrice);

//        holder.rechargeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent moveTodetail = new Intent(context, customerDetailForm_activity.class);
//                moveTodetail.putExtra("EXTRA_SPEED_MBPS", plan.speedMBps);
//                moveTodetail.putExtra("EXTRA_TIME_PERIOD", plan.timePeriod);
//                moveTodetail.putExtra("EXTRA_PLAN_PRICE", plan.planPrice);
//                context.startActivity(moveTodetail);
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return internetPlan.size(); // Return the size of the list to display the correct number of items
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView internetSpeed, timePeriod, rechargePlan;
        LinearLayout rechargeLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            internetSpeed = itemView.findViewById(R.id.speedMBps);
            timePeriod = itemView.findViewById(R.id.monthlyPlan);
            rechargePlan = itemView.findViewById(R.id.rechargePlanPrice);
            rechargeLayout = itemView.findViewById(R.id.planLayout);
        }
    }
}