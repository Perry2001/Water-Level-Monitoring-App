package com.example.myapplication.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.Adapter.BlockedUserAdapter;
import com.example.myapplication.Model.AllUserModel;
import com.example.myapplication.Model.BlockedUserModel;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;


public class BlockedUserFragment extends Fragment {

    BlockedUserAdapter myAdapter;
    ArrayList<BlockedUserModel> list;

    RecyclerView recyclerView;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blocked_user, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);

        list = new ArrayList<>();

        myAdapter = new BlockedUserAdapter(getContext(),list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(myAdapter);

        setUpRecyclerview();



        return view;
    }

    private void setUpRecyclerview() {

        databaseReference = FirebaseDatabase.getInstance().getReference("blockedUser");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){

                    if (dataSnapshot.hasChild("isBlock") &&
                            dataSnapshot.hasChild("name") &&
                            dataSnapshot.hasChild("email") &&
                            dataSnapshot.hasChild("userUID")) {

                        boolean isBlocked = (boolean)dataSnapshot.child("isBlock").getValue();

                        String name = dataSnapshot.child("name").getValue().toString();
                        String email = dataSnapshot.child("email").getValue().toString();
                        String userUID = dataSnapshot.child("userUID").getValue().toString();

                        if (isBlocked){
                            list.add(new BlockedUserModel(name,email,userUID));
                        }
                    }

                }
                if (myAdapter != null){
                    myAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", error.getMessage());
            }
        });

        refresh(1000);
    }

    void refresh(int milliseconds){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setUpRecyclerview();
            }
        }, milliseconds);
    }
}