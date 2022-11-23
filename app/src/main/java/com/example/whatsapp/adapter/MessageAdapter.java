package com.example.whatsapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.activities.MessageActivity;
import com.example.whatsapp.model.Chat;
import com.example.whatsapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context mContext;
    private List<Chat> chatList;
    private String imgUrl;

    FirebaseUser firebaseUser;

    public MessageAdapter(Context mContext, List<Chat> chatList, String imgUrl){
        this.mContext = mContext;
        this.chatList = chatList;
        this.imgUrl = imgUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){

        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);
        return new ViewHolder(view);
        }else{

            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat = chatList.get(position);
        holder.show_message.setText(chat.getMessage());

        if(imgUrl.equals("default")){
            holder.profileImg.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mContext).load(imgUrl).into(holder.profileImg);

        }

        if(position == chatList.size()-1){
            if (chatList.get(position).getSeen()){
                holder.txt_seen.setText("Seen");
            }else{
                holder.txt_seen.setText("Delivered");
            }
        }else{
            holder.txt_seen.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{



        TextView show_message;
        ImageView profileImg;
        TextView txt_seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profileImg = itemView.findViewById(R.id.profile_img);
            txt_seen = itemView.findViewById(R.id.txt_seen);
        }
    }

    @Override
    public int getItemViewType(int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}
