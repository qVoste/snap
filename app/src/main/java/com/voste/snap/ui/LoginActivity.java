package com.voste.snap.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.voste.snap.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginButton.setOnClickListener(v -> {
            if (binding.loginEmailEditText.getText().toString().isEmpty() || binding.loginPasswordEditText.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            }else{
                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.loginEmailEditText.getText().toString(), binding.loginPasswordEditText.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                        });
            }
        });
        binding.backButton.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ChoiceActivity.class)));
    }
}