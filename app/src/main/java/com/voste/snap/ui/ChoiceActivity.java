package com.voste.snap.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.voste.snap.databinding.ActivityChoiceBinding;

public class ChoiceActivity extends AppCompatActivity{
    private ActivityChoiceBinding binding;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        binding = ActivityChoiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginButton.setOnClickListener(v -> {
            startActivity(new Intent(ChoiceActivity.this, LoginActivity.class));
        });

        binding.registerButton.setOnClickListener(v -> {
            startActivity(new Intent(ChoiceActivity.this, RegisterActivity.class));
        });
    }
}
