package com.example.myapplication.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.AllUserModel;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class AllUserAdapter extends RecyclerView.Adapter<AllUserAdapter.MyViewHolder> {

    Context context;
    ArrayList<AllUserModel> list;
    private int clickedPosition = -1;

    public AllUserAdapter(Context context, ArrayList<AllUserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AllUserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_user_list_recyclerview, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllUserAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(list.get(position).getName());
        holder.email.setText(list.get(position).getEmail());
        holder.nameSelected = list.get(holder.getAdapterPosition()).getName();

        holder.blockUserBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                final String nameSelected = holder.nameSelected;
                final String userUID = list.get(position).getUserUID();
                final String emailSelected = list.get(position).getEmail();

                Dialog blockUserDialog = new Dialog(context);
                blockUserDialog.setContentView(R.layout.block_user_dialog);
                blockUserDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                blockUserDialog.setCancelable(false);
                blockUserDialog.show();

                AppCompatButton yesBtn, noBtn;
                TextView nameOfUserBlock;
                nameOfUserBlock = blockUserDialog.findViewById(R.id.blockUserName_Textview);
                yesBtn = blockUserDialog.findViewById(R.id.yes_Button);
                noBtn = blockUserDialog.findViewById(R.id.no_Button);


                nameOfUserBlock.setText("Are you sure you want \nto block " + nameSelected + " ?");
                yesBtn.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View v) {

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users")
                                .child(userUID);

                        HashMap<String, Object> updateBlockedUser = new HashMap<>();
                        updateBlockedUser.put("isBlock", true);

                        databaseReference.updateChildren(updateBlockedUser)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {



                                        Toast.makeText(context, "User " + nameSelected + " successfully blocked", Toast.LENGTH_LONG).show();
                                        blockUserDialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("TAG", "failed to block user " + nameSelected);
                                        blockUserDialog.dismiss();

                                    }
                                });

                    }
                });

                noBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blockUserDialog.dismiss();
                    }
                });


            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name, email;
        ImageButton blockUserBtn;
        String nameSelected;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_Textview);
            email = itemView.findViewById(R.id.email_Textview);
            blockUserBtn = itemView.findViewById(R.id.blockUser_Button);

        }
    }
}
