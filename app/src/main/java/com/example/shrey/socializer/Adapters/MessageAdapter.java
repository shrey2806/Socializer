package com.example.shrey.socializer.Adapters;


import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shrey.socializer.Models.Messages;
import com.example.shrey.socializer.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<Messages> mMessageList){

        this.mMessageList=mMessageList;
    }




    public MessageViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout,parent,false);


        return new MessageViewHolder(v);
    }




    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageTextLeft;
        public TextView messageTextRight;
       // public CircleImageView profileImage;


        public MessageViewHolder(View itemView) {
            super(itemView);

            messageTextLeft=(TextView) itemView.findViewById(R.id.message_text_left);
            messageTextRight=(TextView) itemView.findViewById(R.id.message_text_right);
           // profileImage=(CircleImageView)itemView.findViewById(R.id.message_profile_pic);

        }
    }


    @Override
    public void onBindViewHolder(MessageViewHolder mholder, int i) {

        mAuth=FirebaseAuth.getInstance();
            String current_user_id=mAuth.getCurrentUser().getUid();

            Messages m = mMessageList.get(i);

            String from_user=m.getFrom();

            if(from_user.equals(current_user_id)){

               mholder.messageTextRight.setText(mMessageList.get(i).getMessage());
               mholder.messageTextRight.setVisibility(View.VISIBLE);
                mholder.messageTextLeft.setVisibility(View.INVISIBLE);

            }else{

                mholder.messageTextLeft.setText(mMessageList.get(i).getMessage());
                mholder.messageTextLeft.setVisibility(View.VISIBLE);
                mholder.messageTextRight.setVisibility(View.INVISIBLE);



            }

           // mholder.messageText.setText(mMessageList.get(i).getMessage());

    }

    @Override
    public int getItemCount() {

        return mMessageList.size();
    }




}
