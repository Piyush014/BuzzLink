package com.piyush.a02_buzzlink_chat_application;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class Splash extends AppCompatActivity {

    LottieAnimationView animationView;
    TextView name, own1, own2;
    Animation bottomAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        animationView = findViewById(R.id.animationView);
        name = findViewById(R.id.logonameimg);
        own1 = findViewById(R.id.ownone);
        own2 = findViewById(R.id.owntwo);

        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        animationView.setSpeed(1.5f); // Speed up the animation
        animationView.playAnimation();

        name.setAnimation(bottomAnim);
        own1.setAnimation(bottomAnim);
        own2.setAnimation(bottomAnim);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Splash.this, login.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 4000); // Reduced delay to 4000ms for faster transition
    }
}
