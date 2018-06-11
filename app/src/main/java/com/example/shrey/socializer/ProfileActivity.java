package com.example.shrey.socializer;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {


    private ImageView pImageView;
    private TextView DisplayName;
    private TextView DisplayStatus;

    private Button sendRequest;
    DatabaseReference mUserDatabase;
    DatabaseReference mFriendReqDatabase;
    DatabaseReference FriendsDatabase;
    DatabaseReference notificationDatabase;
    String current_status;
    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        pImageView=findViewById(R.id.profile_image);
        DisplayName=findViewById(R.id.display_name);
        DisplayStatus=findViewById(R.id.display_status);

        sendRequest=findViewById(R.id.send_request_btn);
        final String user_id=getIntent().getStringExtra("user_id");

        current_status="not_friends";

        currentUser= FirebaseAuth.getInstance().getCurrentUser();

        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        mFriendReqDatabase=FirebaseDatabase.getInstance().getReference().child("Friend_requests");
        FriendsDatabase=FirebaseDatabase.getInstance().getReference().child("Friends");
        notificationDatabase=FirebaseDatabase.getInstance().getReference().child("notifications");

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name=dataSnapshot.child("name").getValue(String.class);
                String display_status=dataSnapshot.child("status").getValue(String.class);
                String image=dataSnapshot.child("image").getValue(String.class);

                DisplayName.setText(display_name);
                DisplayStatus.setText(display_status);
                Glide.with(ProfileActivity.this).load(image).placeholder(R.drawable.img).into(pImageView);


                //_________________Update button on entry _______________//



                mFriendReqDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id)){
                            String req_type=dataSnapshot.child(user_id).child("request_type").getValue(String.class);

                            if(req_type.equals("received")){
                                current_status="request_received";
                                sendRequest.setText("ACCEPT FRIEND REQUEST");


                            }else if(req_type.equals("sent")){
                                current_status="req_sent";
                                sendRequest.setText("CANCEL FRIEND REQUEST");

                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                FriendsDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id)){
                            current_status="friends";
                            sendRequest.setText(R.string.unfriend_person);


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });



        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendRequest.setEnabled(false);

                //___________________SEND REQUEST FEATURE_________________________//

                if(current_status.equals("not_friends")){

                    mFriendReqDatabase.child(currentUser.getUid()).child(user_id).child("request_type").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            mFriendReqDatabase.child(user_id).child(currentUser.getUid()).child("request_type")
                                                    .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    HashMap<String,String> notificationData=new HashMap<>();
                                                    notificationData.put("from",currentUser.getUid());
                                                    notificationData.put("type","request");


                                                    notificationDatabase.child(user_id).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            sendRequest.setEnabled(true);
                                                            current_status="req_sent";
                                                            sendRequest.setText("CANCEL FRIEND REQUEST");



                                                        }
                                                    });


                                                    Toast.makeText(ProfileActivity.this,"Request Sent",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }else{
                                            Toast.makeText(ProfileActivity.this,"Failed Sending Request ",Toast.LENGTH_SHORT).show();

                                        }
                                    sendRequest.setEnabled(true);

                                }
                            });


                }


                //_____________________CANCEL REQUEST FEATURE______________________

                if(current_status.equals("req_sent")){

                    mFriendReqDatabase.child(currentUser.getUid()).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mFriendReqDatabase.child(user_id).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                            sendRequest.setEnabled(true);
                                            current_status="not_friends";
                                            sendRequest.setText("SEND FRIEND REQUEST");

                                    }
                                });
                            }
                        }
                    });


                }


              if(current_status.equals("request_received")) {

                    final String currentDate= DateFormat.getDateTimeInstance().format(new Date());
                    FriendsDatabase.child(currentUser.getUid()).child(user_id).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FriendsDatabase.child(user_id).child(currentUser.getUid()).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendReqDatabase.child(currentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendReqDatabase.child(user_id).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    sendRequest.setEnabled(true);
                                                    current_status="friends";
                                                    sendRequest.setText("UNFRIEND THIS PERSON");
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });



              }

              if(current_status.equals("friends")){

                    FriendsDatabase.child(currentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FriendsDatabase.child(user_id).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                        sendRequest.setEnabled(true);
                                        current_status="not_friends";
                                        sendRequest.setText("SEND FRIEND REQUEST");
                                }
                            });
                        }
                    });

              }



            }
        });

    }




}
