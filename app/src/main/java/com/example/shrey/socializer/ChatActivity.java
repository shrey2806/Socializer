package com.example.shrey.socializer;

import android.support.v7.app.AppCompatActivity;

import android.content.Context;
import android.support.v7.app.ActionBar;

import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;


import com.example.shrey.socializer.Adapters.MessageAdapter;
import com.example.shrey.socializer.Models.Messages;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity{


    private String chatUser;
    private android.support.v7.widget.Toolbar mChatToolbar;
    private DatabaseReference mrootref;
    private FirebaseAuth mAuth;
    private String mCurrentuser;
    private DatabaseReference mCurrentuserRef;

    private EditText chatmessageView;
    private ImageButton chatSendbtn;
    String mCurrentusername;

    private RecyclerView mMessagesList;
    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager mlinearLayout;
    private MessageAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatUser = getIntent().getStringExtra("userid");


        mChatToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);
        ActionBar actionBar = getSupportActionBar();


        chatmessageView=(EditText)findViewById(R.id.chatEditText);
        chatSendbtn=(ImageButton)findViewById(R.id.send_btn);



        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View ActionBarview = inflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(ActionBarview);

        getSupportActionBar().setTitle(chatUser);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mAdapter=new MessageAdapter(messagesList);


        mMessagesList=(RecyclerView)findViewById(R.id.messages_list);
        mlinearLayout=new LinearLayoutManager(this);
        mMessagesList.setLayoutManager(mlinearLayout);
        mMessagesList.setAdapter(mAdapter);





        mrootref = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        mCurrentuser = mAuth.getCurrentUser().getUid();
        mCurrentuserRef = mrootref.child("users").child(mCurrentuser);




        mCurrentuserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 mCurrentusername = dataSnapshot.child("name").getValue(String.class);





                mrootref.child("Chat").child(mCurrentusername).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild(chatUser)) {
                            Map chatUserMap = new HashMap();
                            chatUserMap.put("Chat/" + mCurrentusername + "/" + chatUser, "default");
                            chatUserMap.put("Chat/" + chatUser + "/" + mCurrentusername, "default");

                            mrootref.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        Log.d("TAG CHAT", databaseError.getMessage().toString());

                                    }

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                loadMessages();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //

        chatSendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
                chatmessageView.setText("");

            }
        });


    }

    private void loadMessages() {
        mrootref.child("messages").child(mCurrentusername).child(chatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message=dataSnapshot.getValue(Messages.class);

                //Log.d("Valuemessage",dataSnapshot.getChildren().toString());

                if(message==null) {
                    return;
                }
                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
//        });
//        mrootref.child("messages").child(mCurrentusername).child(chatUser).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//
////                if (!dataSnapshot.hasChild("message")) {
////                    Map chatUserMap = new HashMap();
////                    chatUserMap.put("messages/" + mCurrentusername + "/" + chatUser+"/"+"message/", "");
////                    chatUserMap.put("messages/" + chatUser + "/" + mCurrentusername+"/"+"message/", "");
////
////                    mrootref.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
////                        @Override
////                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
////                            if (databaseError != null) {
////                                Log.d("TAG CHAT", databaseError.getMessage().toString());
////
////                            }
////
////                        }
////                    });
////                }
//
//
//                Messages message=dataSnapshot.getValue(Messages.class);
//
//                if(message==null){
//                    return;
//                }
//
//                messagesList.add(message);
//                mAdapter.notifyDataSetChanged();
//
//            }

//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
        });
    }

    private void sendMessage(){
        String message=chatmessageView.getText().toString();
        if(!TextUtils.isEmpty(message)){

            String current_user_ref="messages"+"/"+mCurrentusername+"/"+chatUser;
            String chat_user_ref="messages"+"/"+chatUser+"/"+mCurrentusername;

            DatabaseReference user_message_push=mrootref.child("messages").child(mCurrentusername).child(chatUser).push();
            String push_id=user_message_push.getKey();
//            Map messageMap=new HashMap();
//            messageMap.put(current_user_ref+"/"+"message",message);
//            messageMap.put(chat_user_ref+"/"+"message",message);
//
//            mrootref.updateChildren(messageMap, new DatabaseReference.CompletionListener() {
//                @Override
//                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//
//                }
//            });
//
            Map messageMap=new HashMap();
            messageMap.put("message",message);


            Map messageUsermap=new HashMap();
            messageUsermap.put(current_user_ref+"/"+push_id,messageMap);
            messageUsermap.put(chat_user_ref+"/"+push_id,messageMap);

            mrootref.updateChildren(messageUsermap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                   if(databaseError!=null)
                    Log.d("Hello",databaseError.getMessage().toString());
                }
            });

       }

    }


}
