package com.piyush.a02_buzzlink_chat_application;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class FullScreenImageFragment extends Fragment {

    private static final String ARG_IMAGE_URL = "imageUrl";
    private String imageUrl;

    public static FullScreenImageFragment newInstance(String imageUrl) {
        FullScreenImageFragment fragment = new FullScreenImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_full_screen_image, container, false);
        ImageView fullScreenImageView = view.findViewById(R.id.fullScreenImageView);

        if (getArguments() != null) {
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
        }

        // Load the image using Glide
        Glide.with(this)
                .load(imageUrl)
                .into(fullScreenImageView);

        return view;
    }
}
