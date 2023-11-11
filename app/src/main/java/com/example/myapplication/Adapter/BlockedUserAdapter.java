package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.BlockedUserModel;
import com.example.myapplication.R;

import java.util.ArrayList;

public class BlockedUserAdapter extends RecyclerView.Adapter<BlockedUserAdapter.MyViewHolder> {
    Context context;
    ArrayList<BlockedUserModel> list;

    public BlockedUserAdapter(Context context, ArrayList<BlockedUserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public BlockedUserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.blocked_user_list_recyclerview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockedUserAdapter.MyViewHolder holder, int position) {
        holder.name.setText(list.get(position).getName());
        holder.email.setText(list.get(position).getEmail());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, email;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name  = itemView.findViewById(R.id.name_Textview);
            email = itemView.findViewById(R.id.email_Textview);
        }
    }
}
