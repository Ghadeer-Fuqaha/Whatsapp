package com.example.whatsapp.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsapp.R;
import com.example.whatsapp.adapter.userAdapter;
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
import java.util.List;

public class ChatFragment extends Fragment {

    RecyclerView recyclerView;
    userAdapter userAdapter;
    List<User> mUsers;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    List<String> userList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_chat_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Here write your code

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){

                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getSender().equals(firebaseUser.getUid())){
                        if(!userList.contains(chat.getReceiver()))
                        userList.add(chat.getReceiver());
                    }else if(chat.getReceiver().equals(firebaseUser.getUid())){
                        if(!userList.contains(chat.getSender()))
                        userList.add(chat.getSender());
                    }
                }

                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readChats() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);

                    for(int i=0;i<userList.size();i++){
                        if(user.getId().equals(userList.get(i))){

                            if(mUsers.size()!=0){
                                for(int j=0;j<mUsers.size();j++){
                                    if(!user.getId().equals(mUsers.get(j).getId())){
                                        mUsers.add(user);
                                    }
                                }
                            }else{
                                mUsers.add(user);
                            }
                        }
                    }
                }
                userAdapter = new userAdapter(getContext(),mUsers,true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}