package com.piyush.a02_buzzlink_chat_application;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    private static final int STORAGE_PERMISSION_CODE = 101;

    private CircleImageView profileImage;
    private TextInputEditText editName;
    private MaterialButton saveButton;
    private Uri imageUri;
    private DatabaseReference userRef;
    private StorageReference profilePicRef;
    private String currentUserId;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("user").child(currentUserId);
        profilePicRef = FirebaseStorage.getInstance().getReference("profile_images");

        // Load existing user data from Firebase (profile picture and name)
        loadUserData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        profileImage = view.findViewById(R.id.profile_image);
        editName = view.findViewById(R.id.edit_name);
        saveButton = view.findViewById(R.id.save_button);

        // Open image picker on profile image click
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for storage permission before opening image picker
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        openImagePicker();
                    } else {
                        requestStoragePermission();
                    }
                } else {
                    openImagePicker();
                }
            }
        });

        // Save changes on button click
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }

    private void saveChanges() {
        final String name = editName.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getActivity(), "Please enter a name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            final StorageReference fileRef = profilePicRef.child(currentUserId + ".jpg");
            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        fileRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    String imageUrl = task.getResult().toString();
                                    updateUserProfile(name, imageUrl);
                                } else {
                                    Toast.makeText(getActivity(), "Error getting image URL", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(getActivity(), "Image upload failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            updateUserProfile(name, null); // Save name even if image is not changed
        }
    }

    private void updateUserProfile(String name, @Nullable String imageUrl) {
        userRef.child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (imageUrl != null) {
                        userRef.child("image").setValue(imageUrl);
                    }
                    Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadUserData() {
        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    DataSnapshot userData = task.getResult();
                    String name = userData.child("name").getValue(String.class);
                    String imageUrl = userData.child("image").getValue(String.class);

                    editName.setText(name != null ? name : ""); // Set name if available

                    // Load profile image with Glide
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(getActivity())
                                .load(imageUrl)
                                .placeholder(R.drawable.photocamera) // Default placeholder image
                                .error(R.drawable.man) // Error image in case of failure
                                .into(profileImage);
                    } else {
                        profileImage.setImageResource(R.drawable.man); // Default image if no URL
                    }
                }
            }
        });
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(getContext(), "Storage permission is required to select a profile picture", Toast.LENGTH_SHORT).show();
        }
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(getContext(), "Permission denied to access storage", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
