package com.example.shrey.socializer.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import com.example.shrey.socializer.ChatActivity;
import com.example.shrey.socializer.Models.Conversation;
import com.example.shrey.socializer.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private RecyclerView mChatList;

    private DatabaseReference userDatabase, messageDatabase, convDatabase;

    private View mMainView;

    private String currentUserId;

    private Context ctx;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_chats, container, false);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mChatList = mMainView.findViewById(R.id.chats_recycler_view);
        mChatList.setHasFixedSize(true);
        //  mChatList.setLayoutManager(new LinearLayoutManager(getContext()));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        // adds the conv. to the top of the list;
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        mChatList.setLayoutManager(linearLayoutManager);

        ctx=getActivity().getApplicationContext();

        userDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        userDatabase.keepSynced(true);
        messageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(currentUserId);
       // messageDatabase.keepSynced(true);
        convDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId);
        convDatabase.keepSynced(true);
        return mMainView;

    }

    @Override
    public void onStart() {
        super.onStart();

        Query getConvQuery = convDatabase.orderByChild("timestamp");


        FirebaseRecyclerAdapter<Conversation, ConversationViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter
                <Conversation, ConversationViewHolder>(Conversation.class, R.layout.conversation_item_layout
                , ConversationViewHolder.class, getConvQuery) {
            @Override
            protected void populateViewHolder(final ConversationViewHolder viewHolder, final Conversation model, int position) {

                final String chat_list_user_id = getRef(position).getKey();

                // Add Details of the user

                userDatabase.child(chat_list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String name = dataSnapshot.child("name").getValue().toString();
                        viewHolder.setName(name);
                        String image = dataSnapshot.child("thumb_image").getValue().toString();
                        viewHolder.setImage(image,ctx);

                        if (dataSnapshot.hasChild("online")) {
                            String onlineString = dataSnapshot.child("online").getValue().toString();
                            viewHolder.setOnlineIcon(onlineString);

                        }

                        //Adding click listener on the list item
                        viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent chati = new Intent(getContext(), ChatActivity.class);
                                chati.putExtra("userid",chat_list_user_id);
                                chati.putExtra("username",name);
                                startActivity(chati);
                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });


                Query lastMessageqry = messageDatabase.child(chat_list_user_id).limitToLast(1);

                lastMessageqry.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String messg = dataSnapshot.child("message").getValue().toString();

                        viewHolder.setLastMessage(messg, model.isSeen());

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
                });


            }
        };

        mChatList.setAdapter(firebaseRecyclerAdapter);

    }


    public static class ConversationViewHolder extends RecyclerView.ViewHolder {

        View mview;

        public ConversationViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
        }


        public void setName(String name) {

            TextView displayNameView = mview.findViewById(R.id.conv_user_name);
            displayNameView.setText(name);

        }

        public void setImage(String thumb_image, Context ctx) {

            CircleImageView displayImageView = mview.findViewById(R.id.conv_user_image);
            Glide.with(ctx).load(thumb_image).placeholder(R.drawable.acc_image).into(displayImageView);

        }

        public void setLastMessage(String message, boolean isSeen) {

            TextView messageTextView = mview.findViewById(R.id.conv_last_message_textv);
            messageTextView.setText(message);

            if (!isSeen) {
                messageTextView.setTypeface(messageTextView.getTypeface(), Typeface.BOLD);


            } else {
                messageTextView.setTypeface(messageTextView.getTypeface(), Typeface.NORMAL);

            }
        }


        public void setOnlineIcon(String online) {
            ImageView online_icon_view = mview.findViewById(R.id.conv_online_icon);
            if (online.equals("true")) {
                online_icon_view.setVisibility(View.VISIBLE);
            } else {
                online_icon_view.setVisibility(View.INVISIBLE);
            }

        }


    }
}
