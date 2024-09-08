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

public class CommunityFragment extends Fragment implements CommunityAdapter.OnAddUserClickListener {

    private RecyclerView communityRecyclerView;
    private CommunityAdapter adapter;
    private ArrayList<CommunityUser> usersArrayList;
    private FirebaseDatabase database;
    private String currentUserId;

    public CommunityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        communityRecyclerView = view.findViewById(R.id.communityRecyclerView);
        communityRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        usersArrayList = new ArrayList<>();
        adapter = new CommunityAdapter(getContext(), usersArrayList, this);
        communityRecyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadCommunityUsers();

        return view;
    }

    private void loadCommunityUsers() {
        DatabaseReference reference = database.getReference().child("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArrayList.clear(); // Clear the list to avoid duplicate entries
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CommunityUser user = dataSnapshot.getValue(CommunityUser.class);
                    if (user != null && !user.getUserId().equals(currentUserId)) {
                        usersArrayList.add(user);
                        // Debugging: Log user data
                        Log.d("CommunityFragment", "User added: " + user.getUserName());
                    }
                }
                adapter.notifyDataSetChanged();
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

    @Override
    public void onAddUserClick(CommunityUser user) {
        DatabaseReference chatRef = database.getReference().child("userChats").child(currentUserId).child(user.getUserId());
        chatRef.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "User added to chats successfully", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Failed to add user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
