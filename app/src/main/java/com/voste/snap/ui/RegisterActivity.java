package com.voste.snap.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;
import com.voste.snap.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //регистрация
        binding.registerButton.setOnClickListener(v -> {
            if (binding.registerEmailEditText.getText().toString().isEmpty() || binding.registerPasswordEditText.getText().toString().isEmpty()
                    || binding.registerNicknameEditText.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            }else{
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.registerEmailEditText.getText().toString(), binding.registerPasswordEditText.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                HashMap<String, String> userInfo = new HashMap<>();
                                userInfo.put("email", binding.registerEmailEditText.getText().toString());
                                userInfo.put("username", binding.registerNicknameEditText.getText().toString());
                                userInfo.put("profileImage", "");  // или просто оставить поле отсутствующим
                                userInfo.put("chats", "");

                                FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                        .setValue(userInfo);

                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            }
                        });
            }
        });
        //переход на вход
        binding.backButton.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, ChoiceActivity.class)));
    }
}