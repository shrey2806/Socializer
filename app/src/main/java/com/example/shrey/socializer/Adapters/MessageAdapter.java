package com.example.shrey.socializer.Adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shrey.socializer.Models.Messages;
import com.example.shrey.socializer.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> mMessageList;

    public MessageAdapter(List<Messages> mMessageList){

        this.mMessageList=mMessageList;
    }




    public MessageViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout,parent,false);


        return new MessageViewHolder(v);
    }




    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText;
        public CircleImageView profileImage;


        public MessageViewHolder(View itemView) {
            super(itemView);

            messageText=(TextView) itemView.findViewById(R.id.message_text);
            profileImage=(CircleImageView)itemView.findViewById(R.id.message_profile_pic);

        }
    }


    @Override
    public void onBindViewHolder(MessageViewHolder mholder, int i) {


            mholder.messageText.setText(mMessageList.get(i).getMessage());

    }

    @Override
    public int getItemCount() {

        return mMessageList.size();
    }




}
