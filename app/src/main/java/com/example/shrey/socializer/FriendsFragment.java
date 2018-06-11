package com.example.shrey.socializer;


import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.internal.operators.flowable.FlowableOnErrorReturn;


public class FriendsFragment extends Fragment {

    private RecyclerView muserlist;
    private DatabaseReference mdatabase;
    private View mMainView;
    private String userid;


    public FriendsFragment() {



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//
        mMainView=inflater.inflate(R.layout.fragment_friends,container,false);

        userid= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        muserlist=(RecyclerView)mMainView.findViewById(R.id.friends_recyclerview);
        muserlist.setLayoutManager(new LinearLayoutManager(getContext()));

        mdatabase= FirebaseDatabase.getInstance().getReference().child("users");

        return mMainView;
    }


    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users,FriendsViewHolder> adapter =new FirebaseRecyclerAdapter<Users, FriendsViewHolder>(
                Users.class,
                R.layout.users_layout,
                FriendsViewHolder.class,
                mdatabase) {
            @Override
            protected void populateViewHolder(FriendsViewHolder viewHolder, final Users model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setUserStatus(model.getStatus());
               viewHolder.setuserImage(model.getThumb_image(),getContext());




               viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {


                       Intent chatIntent=new Intent(getContext(),ChatActivity.class);
//                       Log.i("hello", ""+m);
                       chatIntent.putExtra("userid",model.name);
                       startActivity(chatIntent);

                   }
               });


            }


        };
        muserlist.setAdapter(adapter);


    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        View mview;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mview=itemView;

        }
        public  void setName(String name){
            TextView usernameView=mview.findViewById(R.id.users_name);
            usernameView.setText(name);
        }

        public void setUserStatus(String status){
            TextView userStatus=mview.findViewById(R.id.user_status);
            userStatus.setText(status);


        }
        public void setuserImage(String thumb_image, Context ctx){
            CircleImageView userImageview=mview.findViewById(R.id.user_image);
            Glide.with(ctx).load(thumb_image).placeholder(R.drawable.acc_image).into(userImageview);
        }

    }


}
