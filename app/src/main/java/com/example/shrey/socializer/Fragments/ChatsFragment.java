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
public class ChatsFragment extends Fragment {

    private RecyclerView mChatList;

    private DatabaseReference userDatabase,messageDatabase, convDatabase;

    private View mMainView;

    private String currentUserId;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView=inflater.inflate(R.layout.fragment_chats, container, false);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mChatList = mMainView.findViewById(R.id.chats_recycler_view);
        mChatList.setHasFixedSize(true);
      //  mChatList.setLayoutManager(new LinearLayoutManager(getContext()));

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        // adds the conv. to the top of the list;
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        mChatList.setLayoutManager(linearLayoutManager);


        userDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        userDatabase.keepSynced(true);
        messageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(currentUserId);
        messageDatabase.keepSynced(true);
        convDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId);
        convDatabase.keepSynced(true);
        return mMainView;
    }

}
