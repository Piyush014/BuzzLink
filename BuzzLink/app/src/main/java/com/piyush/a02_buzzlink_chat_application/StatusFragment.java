package com.piyush.a02_buzzlink_chat_application;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class StatusFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private ImageView userImg;
    private TextView username;
    private TextView userStatus;
    private ImageView cameraIcon;
    private LinearLayout statusListContainer;

    private FirebaseDatabase database;
    private StorageReference storageReference;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        statusListContainer = view.findViewById(R.id.statusListContainer);
        cameraIcon = view.findViewById(R.id.cameraIcon);

        database = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadUserStatus();

        // Camera icon click listener
        cameraIcon.setOnClickListener(v -> openImageOptions());

        return view;
    }

    private void openImageOptions() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Intent chooserIntent = Intent.createChooser(pickIntent, "Select or take a new Picture");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});

        startActivityForResult(chooserIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri imageUri = null;

            if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                imageUri = data.getData();
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                imageUri = data.getData();
            }

            if (imageUri != null) {
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference fileRef = storageReference.child("userstatus").child(currentUserId + ".jpg");

        fileRef.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    saveImageToDatabase(downloadUrl);
                });
            } else {
                Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveImageToDatabase(String imageUrl) {
        DatabaseReference statusRef = database.getReference("userstatus").child(currentUserId);
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("imageUrl", imageUrl);
        statusRef.updateChildren(statusMap);
    }

    private void loadUserStatus() {
        DatabaseReference userRef = database.getReference("user").child(currentUserId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("userName").getValue(String.class);
                    String status = snapshot.child("status").getValue(String.class);
                    String profilePicUrl = snapshot.child("profilepic").getValue(String.class);

                    View statusItemView = LayoutInflater.from(getContext()).inflate(R.layout.user_item, statusListContainer, false);

                    userImg = statusItemView.findViewById(R.id.userimg);
                    username = statusItemView.findViewById(R.id.username);
                    userStatus = statusItemView.findViewById(R.id.userstatus);

                    username.setText(name);
                    userStatus.setText(status);

                    Glide.with(getContext())
                            .load(profilePicUrl)
                            .placeholder(R.drawable.man)
                            .into(userImg);

                    statusItemView.setOnClickListener(v -> {
                        DatabaseReference statusRef = database.getReference("userstatus").child(currentUserId);
                        statusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                                    if (imageUrl != null) {
                                        // Start FullScreenImageFragment
                                        FullScreenImageFragment fragment = FullScreenImageFragment.newInstance(imageUrl);
                                        getActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.fragment_container, fragment) // Make sure to use the correct container ID
                                                .addToBackStack(null)
                                                .commit();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle errors here
                            }
                        });
                    });

                    statusListContainer.addView(statusItemView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }
}
