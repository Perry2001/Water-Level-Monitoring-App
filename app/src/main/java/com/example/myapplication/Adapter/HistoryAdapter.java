package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.HistoryModel;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        String id = model.getId();

        holder.deleteBtn.setOnClickListener(v->{
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("waterPercentageHistory")
                    .child(id);

            databaseReference.removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(context.getApplicationContext(), "Remove Success", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(context.getApplicationContext(), "Failed to remove" + task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();

                            }
                        }
                    });

        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView waterPercentage, dateAndTime;
        ImageButton deleteBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            waterPercentage = itemView.findViewById(R.id.waterPercentage_Textview);
            dateAndTime = itemView.findViewById(R.id.dateAndTime_Textview);
            deleteBtn = itemView.findViewById(R.id.delete_Button);
        }
    }
}
