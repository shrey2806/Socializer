package com.example.shrey.socializer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shrey.socializer.Models.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private RecyclerView muserlist;
    private DatabaseReference mdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mtoolbar=findViewById(R.id.user_appbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mdatabase= FirebaseDatabase.getInstance().getReference().child("users");

        muserlist=findViewById(R.id.users_list);
        //muserlist.setHasFixedSize(true);
        //comment this out
        muserlist.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();
        //We need a viewHolder and a model class.
        //Defining viewHolder in same class.
        FirebaseRecyclerAdapter<Users,UsersViewHolder> adapter =new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,
                R.layout.users_layout,
                UsersViewHolder.class,
                mdatabase) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, final Users model, int position) {



                viewHolder.setName(model.getName());
                viewHolder.setUserStatus(model.getStatus());
                viewHolder.setuserImage(model.getThumb_image());
                final String userId=getRef(position).getKey();

                //Set On click listener for whole view
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent=new Intent(UsersActivity.this,ProfileActivity.class);
                        profileIntent.putExtra("user_id",userId);


                        startActivity(profileIntent);


                    }
                });
            }
        };
        muserlist.setAdapter(adapter);


    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mview;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mview=itemView;

        }
        public  void setName(String name){
            TextView usernameView=mview.findViewById(R.id.request_display_name);
            usernameView.setText(name);
        }

        public void setUserStatus(String status){
            TextView userStatus=mview.findViewById(R.id.user_status);
            userStatus.setText(status);


        }
        public void setuserImage(String thumb_image){
            CircleImageView userImageview=mview.findViewById(R.id.request_display_image);
            Glide.with(mview.getContext()).load(thumb_image).placeholder(R.drawable.acc_image).into(userImageview);
        }

    }

}
