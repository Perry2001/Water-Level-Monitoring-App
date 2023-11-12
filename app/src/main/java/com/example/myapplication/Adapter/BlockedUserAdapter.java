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

import com.example.myapplication.Model.BlockedUserModel;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

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
    public void onBindViewHolder(@NonNull BlockedUserAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(list.get(position).getName());
        holder.email.setText(list.get(position).getEmail());
        holder.nameSelected = list.get(position).getName();

        holder.unBlockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String  nameSelected = holder.nameSelected;
                final String userUID = list.get(position).getUserUID();
                final String emailSelected = list.get(position).getEmail();


                Dialog unBlockUserDialog = new Dialog(context);
                unBlockUserDialog.setContentView(R.layout.unblock_user_dialog);
                unBlockUserDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                unBlockUserDialog.setCancelable(false);
                unBlockUserDialog.show();

                AppCompatButton yesBtn, noBtn;
                TextView name;
                yesBtn = unBlockUserDialog.findViewById(R.id.yes_Button);
                noBtn = unBlockUserDialog.findViewById(R.id.no_Button);
                name = unBlockUserDialog.findViewById(R.id.unBlockUserName_Textview);

                name.setText("Unblock " + nameSelected + " ?");

                yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        HashMap<String, Object> updateUnBlockUser = new HashMap<>();
                        updateUnBlockUser.put("isBlock", false);

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("blockedUser").child(userUID);

                        databaseReference.removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "User " + nameSelected + " successfully unblocked", Toast.LENGTH_LONG).show();
                                        unBlockUserDialog.dismiss();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("TAG", "failed to unblock user " + nameSelected);
                                        unBlockUserDialog.dismiss();

                                    }
                                });

                        HashMap<String, Object> unBlockedUser = new HashMap<>();
                        unBlockedUser.put("name", nameSelected);
                        unBlockedUser.put("email", emailSelected);
                        unBlockedUser.put("userUID", userUID);
                        unBlockedUser.put("isBlock", false);

                        DatabaseReference unBlockUserDF = FirebaseDatabase.getInstance()
                                .getReference("users").child(userUID);

                        unBlockUserDF.updateChildren(unBlockedUser)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("TAG", "Updating database success");

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("TAG", "failed to update");

                                    }
                                });



                    }
                });

                noBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        unBlockUserDialog.dismiss();
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, email;
        ImageButton unBlockBtn;
        String nameSelected;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name  = itemView.findViewById(R.id.name_Textview);
            email = itemView.findViewById(R.id.email_Textview);
            unBlockBtn = itemView.findViewById(R.id.unBlockUser_Button);
        }
    }
}
