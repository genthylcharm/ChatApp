package com.example.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatfirebase.R;
import com.example.chatfirebase.databinding.ActivitySplasscreenBinding;

public class SplasscreenActivity extends AppCompatActivity {
    private ActivitySplasscreenBinding binding;
    private static final int SPLASH_SCREEN = 5000;
    //Variables
    Animation topAnim, bottomAnim;
    ImageView imageView;
    TextView logo, slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplasscreenBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        //Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
// Hooks
        imageView = findViewById(R.id.splashImg);
        logo = findViewById(R.id.logoText);
        slogan = findViewById(R.id.sloganText);

        imageView.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplasscreenActivity.this, SigInActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN);
    }
}