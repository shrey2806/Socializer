package com.example.shrey.socializer.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shrey.socializer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    String currentUserId;

    RecyclerView mRequestsList;

    DatabaseReference mUserDatabase,mRequestDatabase , mFriendDatabase;



    View mainView;


    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       mainView = inflater.inflate(R.layout.fragment_request, container, false);

       currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

       mRequestsList = mainView.findViewById(R.id.Request_recycler_view);
       mRequestsList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);


       mRequestsList.setLayoutManager(linearLayoutManager);

       mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users");

       mRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_requests").child(currentUserId);

       mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");




       return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
