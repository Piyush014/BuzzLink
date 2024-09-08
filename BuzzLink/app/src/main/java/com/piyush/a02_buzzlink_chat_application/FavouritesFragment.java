package com.piyush.a02_buzzlink_chat_application;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment {

    private RecyclerView favouritesRecyclerView;
    private List<Users> favouriteUsersList;
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private UserAdpter userAdpter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        // Initialize views
        favouritesRecyclerView = view.findViewById(R.id.favouritesRecyclerView);
        favouriteUsersList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Set up RecyclerView
        favouritesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdpter = new UserAdpter(getContext(), (ArrayList<Users>) favouriteUsersList);
        favouritesRecyclerView.setAdapter(userAdpter);

        // Load favourite users
        loadFavouriteUsers();

        return view;
    }

    private void loadFavouriteUsers() {
        DatabaseReference favouriteUsersRef = database.getReference().child("favourites").child(firebaseAuth.getUid());
        favouriteUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favouriteUsersList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users user = dataSnapshot.getValue(Users.class);
                    if (user != null) {
                        favouriteUsersList.add(user);
                    }
                }
                userAdpter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
