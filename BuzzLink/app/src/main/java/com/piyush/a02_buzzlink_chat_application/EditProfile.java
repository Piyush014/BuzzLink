package com.piyush.a02_buzzlink_chat_application;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextInputEditText editName, editStatus;
    private MaterialButton saveButton;
    private Uri imageUri;
    private DatabaseReference userRef;
    private StorageReference profilePicRef;
    private String currentUserId;

    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profileImage = findViewById(R.id.profile_image);
        editName = findViewById(R.id.edit_name);
        editStatus = findViewById(R.id.edit_status);
        saveButton = findViewById(R.id.save_button);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("user").child(currentUserId);
        profilePicRef = FirebaseStorage.getInstance().getReference("profile_images");

        // Load existing user data from Firebase (profile picture, name, and status)
        loadUserData();

        // Open image picker on profile image click
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        // Save changes on button click
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);  // Display the new image locally
        }
    }

    private void loadUserData() {
        // Use FirebaseDatabase to load user name, status, and profile picture
        // You can add listeners here to fetch from the database and set the values.
        // e.g. Glide.with(this).load(profileImageUrl).into(profileImage);
    }

    private void saveChanges() {
        final String name = editName.getText().toString();
        final String status = editStatus.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(status)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save profile image if a new one is picked
        if (imageUri != null) {
            StorageReference filePath = profilePicRef.child(currentUserId + ".jpg");
            filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    String downloadUrl = task.getResult().toString();
                                    userRef.child("profilepic").setValue(downloadUrl);
                                }
                            }
                        });
                    }
                }
            });
        }

        // Save name and status to Firebase
        userRef.child("name").setValue(name);
        userRef.child("status").setValue(status);
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }
}
