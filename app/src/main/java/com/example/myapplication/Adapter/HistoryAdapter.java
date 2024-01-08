package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.HistoryModel;
import com.example.myapplication.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    Context context;
    ArrayList<HistoryModel> list;

    public HistoryAdapter(Context context, ArrayList<HistoryModel> list){
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.MyViewHolder holder, int position) {
        HistoryModel model = list.get(position);
        holder.waterPercentage.setText(model.getWaterPercentage());
        holder.dateAndTime.setText(model.getDate() + "\n" + model.getTime());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView waterPercentage, dateAndTime;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            waterPercentage = itemView.findViewById(R.id.waterPercentage_Textview);
            dateAndTime = itemView.findViewById(R.id.dateAndTime_Textview);
        }
    }
}
