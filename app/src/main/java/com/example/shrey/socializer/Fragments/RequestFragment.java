package com.example.shrey.socializer.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shrey.socializer.Models.FriendRequest;
import com.example.shrey.socializer.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


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

        // TODO : First query on the database with the friend Requests.
        // TODO : Add these requests in the layout.


        FirebaseRecyclerAdapter<FriendRequest,FriendsReqViewHolder > firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FriendRequest,
                FriendsReqViewHolder>(FriendRequest.class,R.layout.friend_request_layout,
                FriendsReqViewHolder.class,mRequestDatabase) {
            @Override
            protected void populateViewHolder(final FriendsReqViewHolder viewHolder, FriendRequest model, int position) {


                final String request_user_id = getRef(position).getKey();

                //get the name and display image of the user;
                mUserDatabase.child(request_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();

                        viewHolder.setName(name);

                        String img = dataSnapshot.child("thumb_image").getValue().toString();

                        viewHolder.setImage(img);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




            }
        };

    }


    public static class FriendsReqViewHolder extends RecyclerView.ViewHolder{

        View mview;
        public FriendsReqViewHolder(View itemView) {
            super(itemView);

            mview = itemView;
        }


        public void setName(String name){
            TextView DisplayName= mview.findViewById(R.id.request_display_name);
            DisplayName.setText(name);

        }

        public void setImage(String image){
            ImageView imageView = mview.findViewById(R.id.request_display_image);
            Glide.with(mview.getContext()).load(image).placeholder(R.drawable.acc_image).into(imageView);

        }


    }

}
