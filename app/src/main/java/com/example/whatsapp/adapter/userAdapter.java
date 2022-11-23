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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class userAdapter extends RecyclerView.Adapter<userAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUsers;
    private boolean isChat;
    String lastMsg;

    public userAdapter(Context mContext, List<User> userList, boolean isChat){
        this.mContext = mContext;
        this.mUsers = userList;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User user = mUsers.get(position);
        holder.contactName.setText(user.getUsername());


        if(user.getImgUrl().equals("default")){
            holder.profileImg.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mContext).load(user.getImgUrl()).into(holder.profileImg);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("userid",user.getId());
                mContext.startActivity(intent);
            }
        });
        if(isChat){
            if(user.getState().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);

            }else if(user.getState().equals("offline")){

                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }

            lastMessage(user.getId(), holder.contactStatus);
        }else{
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
            holder.contactStatus.setText(user.getStatus());
        }



    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{



        TextView contactName, contactStatus;
        ImageView profileImg;
        ImageView img_on,img_off;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            contactName = itemView.findViewById(R.id.ContactName);
            contactStatus = itemView.findViewById(R.id.ContactStatus);
            profileImg = itemView.findViewById(R.id.imgProfile);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
        }
    }

    private void lastMessage(String userid, TextView lastMessage){
        lastMsg = "";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if(chat.getSender().equals(firebaseUser.getUid())&& chat.getReceiver().equals(userid) ||
                            chat.getSender().equals(userid)&& chat.getReceiver().equals(firebaseUser.getUid())){
                        lastMsg = chat.getMessage();

                    }
                }

                switch (lastMsg){
                    case "":
                        lastMessage.setText("No messages");
                        break;
                    default:
                        lastMessage.setText(lastMsg);
                        break;
                }

                lastMsg = "";


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
