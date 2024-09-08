package com.piyush.a02_buzzlink_chat_application;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private RecyclerView chatRecyclerView;
    private UserAdpter adapter;
    private ArrayList<Users> usersArrayList;
    private FirebaseDatabase database;
    private String currentUserId;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        usersArrayList = new ArrayList<>();
        adapter = new UserAdpter(getContext(), usersArrayList);
        chatRecyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadChatUsers();

        return view;
    }

    private void loadChatUsers() {
        // Reference to chats initiated by the current user
        DatabaseReference userChatsRef = database.getReference().child("userChats").child(currentUserId);

        // Load users with whom you have chats
        userChatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArrayList.clear(); // Clear the list to avoid duplicate entries

                // Load users with whom you have started chats
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users user = dataSnapshot.getValue(Users.class);
                    if (user != null && !isUserInList(user.getUserId())) {
                        usersArrayList.add(user);
                        Log.d("ChatFragment", "Chat user added: " + user.getUserName());
                    }
                }

                adapter.notifyDataSetChanged(); // Update UI with the chat users
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error: " + error.getMessage());
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Error loading data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Check incoming messages from other users and add them to userChats
        DatabaseReference allChatsRef = database.getReference().child("chats");
        allChatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot chatSnapshot) {
                for (DataSnapshot chat : chatSnapshot.getChildren()) {
                    String chatKey = chat.getKey();

                    // Check if the current user's ID is part of the chat
                    if (chatKey != null && chatKey.contains(currentUserId)) {
                        String[] userIds = chatKey.split("_"); // Assuming the chat keys are like "user1Id_user2Id"
                        String otherUserId = null;

                        // Determine the other user's ID in the chat
                        for (String userId : userIds) {
                            if (!userId.equals(currentUserId)) {
                                otherUserId = userId;
                                break;
                            }
                        }

                        if (otherUserId != null) {
                            // Reference to get other user's details
                            DatabaseReference userRef = database.getReference().child("users").child(otherUserId);
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                    Users otherUser = userSnapshot.getValue(Users.class);
                                    if (otherUser != null && !isUserInList(otherUser.getUserId())) {
                                        // Add the other user to the current user's chats
                                        DatabaseReference userChatRef = database.getReference().child("userChats").child(currentUserId).child(otherUser.getUserId());
                                        userChatRef.setValue(otherUser);

                                        usersArrayList.add(otherUser);
                                        Log.d("ChatFragment", "Incoming message user added: " + otherUser.getUserName());
                                        adapter.notifyDataSetChanged(); // Update UI with the new incoming message user
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("FirebaseError", "Error: " + error.getMessage());
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error: " + error.getMessage());
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Error loading data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Helper method to check if the user is already in the list
    private boolean isUserInList(String userId) {
        for (Users user : usersArrayList) {
            if (user.getUserId().equals(userId)) {
                return true;
            }
        }
        return false;
    }
}
