package com.example.fnsolutions;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerRechargeAdapter extends RecyclerView.Adapter<RecyclerRechargeAdapter.ViewHolder> {

    Context context;
    ArrayList<rechargeModal> planListArray;

    RecyclerRechargeAdapter(Context context, ArrayList<rechargeModal> planListArray){
        this.context = context;
        this.planListArray = planListArray;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recharge_plan, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.speed.setText(planListArray.get(position).speedMBps);
//        holder.effectiveMrp.setText(planListArray.get(position).effectiveMrp);
        holder.mrp.setText(planListArray.get(position).mrp);
        holder.time.setText(planListArray.get(position).timePeriod);
        holder.headingMRP.setText(planListArray.get(position).mrp);

        holder.itemView.setOnClickListener(v -> {
            Log.d("planlist", "Clicked on: " + planListArray.get(position).mrp);
            Toast.makeText(context, "Clicked: " + planListArray.get(position).mrp, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return planListArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView speed, effectiveMrp, mrp, time, headingMRP;
        public ViewHolder(View itemView){
            super(itemView);
            speed = itemView.findViewById(R.id.speed);
//            effectiveMrp = itemView.findViewById(R.id.effective_price);
            mrp = itemView.findViewById(R.id.mrp);
            time = itemView.findViewById(R.id.validity);
            headingMRP = itemView.findViewById(R.id.headingMRP);
        }
    }
}