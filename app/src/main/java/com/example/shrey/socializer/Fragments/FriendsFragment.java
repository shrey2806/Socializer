package com.example.shrey.socializer.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shrey.socializer.ChatActivity;
import com.example.shrey.socializer.Models.Friends;

import com.example.shrey.socializer.ProfileActivity;
import com.example.shrey.socializer.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendsFragment extends Fragment {

    private RecyclerView mFriendlist;

    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendDatabase;
    private View mMainView;
    private String userid;


    public FriendsFragment() {


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mFriendlist = mMainView.findViewById(R.id.friends_recyclerview);
        mFriendlist.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(userid);


        return mMainView;
    }


    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> adapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class,
                R.layout.users_layout,
                FriendsViewHolder.class,
                mFriendDatabase) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, final Friends model, int position) {

               // viewHolder.setDate(model.getDate());

                final String list_userid = getRef(position).getKey();

                //Get the information of current user from the database;
                mUsersDatabase.child(list_userid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();


                        viewHolder.setName(userName);
                        viewHolder.setuserImage(userThumb);

                        if(dataSnapshot.hasChild("online")) {

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(userOnline);

                        }


                        viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                CharSequence options[] = new CharSequence[]{"Open Profile", "Send message"};

                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        //Click Event for each item.
                                        if(i == 0){

                                            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                            profileIntent.putExtra("user_id", list_userid);
                                            startActivity(profileIntent);

                                        }

                                        if(i == 1){

                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("userid", list_userid);
                                            chatIntent.putExtra("user_name", userName);
                                            startActivity(chatIntent);

                                        }

                                    }
                                });

                                builder.show();

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }


        };
        mFriendlist.setAdapter(adapter);


    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        View mview;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mview = itemView;

        }


        public  void setName(String name){
            TextView usernameView=mview.findViewById(R.id.users_name);
            usernameView.setText(name);
        }

        public void setuserImage(String thumb_image){
            CircleImageView userImageview=mview.findViewById(R.id.user_image);
            Glide.with(mview.getContext()).load(thumb_image).placeholder(R.drawable.acc_image).into(userImageview);
        }


        public void setUserOnline(String online_status) {
            ImageView userOnlineView = (ImageView) mview.findViewById(R.id.online_icon_imageview);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }

    }


}
