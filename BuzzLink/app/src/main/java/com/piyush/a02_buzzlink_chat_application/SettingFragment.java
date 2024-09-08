package com.piyush.a02_buzzlink_chat_application;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingFragment extends Fragment {

    private CircleImageView userImage;
    private TextView username;
    private RecyclerView settingsList;
    private FirebaseDatabase database;
    private String currentUserId;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "settings_prefs";
    private static final String DARK_MODE_KEY = "dark_mode";

    private String[][] settingsData = {
            {"user", "Account", "Change Account Settings"},
            {"heat", "Favourites", "Add, reorder, remove connections"},
            {"notification", "Notifications", "Message, group & call tones"},
            {"mode", "Dark Mode", "Change the Mode"},
            {"question", "Help", "Help Center, contact us, privacy policy"}
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        // Initialize views
        userImage = view.findViewById(R.id.user_image);
        username = view.findViewById(R.id.username);
        settingsList = view.findViewById(R.id.settings_list);

        // Initialize Firebase references
        database = FirebaseDatabase.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Load user data and settings items
        loadUserData();
        setupSettingsList();

        return view;
    }

    private void loadUserData() {
        DatabaseReference userRef = database.getReference("user").child(currentUserId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("userName").getValue(String.class);
                    String imageUrl = snapshot.child("profilepic").getValue(String.class);

                    username.setText(name);
                    Glide.with(getContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.man)
                            .into(userImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private void setupSettingsList() {
        List<SettingItem> settingsDataList = new ArrayList<>();
        for (String[] setting : settingsData) {
            settingsDataList.add(new SettingItem(setting[0], setting[1], setting[2]));
        }

        SettingsAdapter adapter = new SettingsAdapter(settingsDataList, this::onSettingItemClick);
        settingsList.setLayoutManager(new LinearLayoutManager(getContext()));
        settingsList.setAdapter(adapter);
    }

    private void onSettingItemClick(SettingItem item) {
        if ("Dark Mode".equals(item.getName())) {
            toggleDarkMode();
        }
    }

    private void toggleDarkMode() {
        sharedPreferences = getContext().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DARK_MODE_KEY, !isDarkMode);
        editor.apply();
    }
}