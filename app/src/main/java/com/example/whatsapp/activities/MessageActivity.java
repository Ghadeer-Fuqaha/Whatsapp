package com.example.whatsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.adapter.MessageAdapter;
import com.example.whatsapp.model.Chat;
import com.example.whatsapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_img;
    TextView username;

    ImageView btn_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Chat> mChat;
    RecyclerView recyclerView;

    FirebaseUser firebaseUser;
    Intent intent;
    DatabaseReference reference;

    ValueEventListener seenListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        intent = getIntent();
        String userid = intent.getStringExtra("userid");

        Toolbar toolbar = findViewById(R.id.MessageToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MessageActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_send = (ImageView) findViewById(R.id.btn_send);
        text_send = (EditText) findViewById(R.id.text_send);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = text_send.getText().toString().trim();
                if(!msg.isEmpty()){
                    sendMessage(firebaseUser.getUid(),userid,msg);
                }else{
                    Toast.makeText(MessageActivity.this, "you can't send empty message!!!", Toast.LENGTH_LONG).show();
                }

                text_send.setText("");
            }
        });

        username = (TextView) findViewById(R.id.username);
        profile_img = (CircleImageView) findViewById(R.id.profile_img);



        reference = FirebaseDatabase.getInstance().getReference("users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());

                if(user.getImgUrl().equals("default")){
                    profile_img.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(user.getImgUrl()).into(profile_img);
                }

                readMessages(firebaseUser.getUid(),userid,user.getImgUrl());
                seenMessage(userid);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }//End onCreate method

    private void seenMessage(String userid){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)){

                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("seen",true);

                        reference.child(dataSnapshot.getKey()).updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("seen",false);

        reference.child("Chats").push().setValue(hashMap);
    }

    private void readMessages(String myId, String userId, String ImgUrl){

        mChat = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(myId) && chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) && chat.getSender().equals(myId)){

                        mChat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(getApplicationContext(),mChat,ImgUrl);
                    recyclerView.setAdapter(messageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void state(String state){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getUid());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("state",state);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        state("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        state("offline");
    }
}