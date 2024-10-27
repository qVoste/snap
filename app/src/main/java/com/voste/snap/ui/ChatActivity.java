package com.voste.snap.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.text.SimpleDateFormat;

import com.voste.snap.databinding.ActivityChatBinding;
import com.voste.snap.model.Message;
import com.voste.snap.adapter.MessagesAdapter;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String chatId = getIntent().getStringExtra("chatId");
        loadMessages(chatId);

        binding.sendMessageButton.setOnClickListener(v -> {
            String message = binding.messageEt.getText().toString();
            if (message.isEmpty()) {
                Toast.makeText(this, "Message field cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            String date = simpleDateFormat.format(new Date());
            binding.messageEt.setText(""); //clearing the edit text
            sendMessage(chatId, message, date);
        });
    }
    //отправка сообщений
    private void sendMessage(String chatId, String message, String date) {
        if (chatId == null) return;

        HashMap<String, String> messageInfo = new HashMap<>();
        messageInfo.put("text", message);
        messageInfo.put("ownerId", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        messageInfo.put("date", date);

        FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId)
                .child("messages").push().setValue(messageInfo);
    }

    private void loadMessages(String chatId) {
        if (chatId == null) return;

        FirebaseDatabase.getInstance().getReference().child("Chats")
                .child(chatId).child("messages").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) return;

                        List<Message> messages = new ArrayList<>();
                        for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                            String messageId = messageSnapshot.getKey();
                            String ownerId = Objects.requireNonNull(messageSnapshot.child("ownerId").getValue()).toString();
                            String text = Objects.requireNonNull(messageSnapshot.child("text").getValue()).toString();
                            String date = Objects.requireNonNull(messageSnapshot.child("date").getValue()).toString();

                            messages.add(new Message(messageId, ownerId, text, date));
                        }
                        binding.messagesRv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                        binding.messagesRv.setAdapter(new MessagesAdapter(messages));
                    }
                    @Override
                    //обработчик ошибок FireBase
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}