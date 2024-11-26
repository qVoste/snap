package com.voste.snap.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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

import com.voste.snap.R;
import com.voste.snap.databinding.ActivityChatBinding;
import com.voste.snap.model.Message;
import com.voste.snap.adapter.MessagesAdapter;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private MessagesAdapter messagesAdapter; // Адаптер для RecyclerView
    private List<Message> messages = new ArrayList<>(); // Список сообщений

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String chatId = getIntent().getStringExtra("chatId");

        setupRecyclerView(); // Настройка RecyclerView
        loadMessages(chatId);

        // Обработчик клика по кнопке отправки сообщения
        binding.sendMessageButton.setOnClickListener(v -> {
            String message = binding.inputMessageEditText.getText().toString();
            if (message.isEmpty()) {
                Toast.makeText(this, "Message field cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String date = simpleDateFormat.format(new Date());
            binding.inputMessageEditText.setText(""); // Очищаем поле ввода
            sendMessage(chatId, message, date);
        });

        // Добавляем слушатель на изменения текста
        binding.inputMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Не нужно обрабатывать
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Если есть текст в поле, показываем кнопку отправки, иначе - кнопку аудио
                if (s.toString().trim().isEmpty()) {
                    binding.audioButton.setVisibility(View.VISIBLE);
                    binding.sendMessageButton.setVisibility(View.GONE);
                } else {
                    binding.audioButton.setVisibility(View.GONE);
                    binding.sendMessageButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Не нужно обрабатывать
            }

        });

        binding.backButton.setOnClickListener(v -> startActivity(new Intent(ChatActivity.this, MainActivity.class)));
    }

    // Настройка RecyclerView
    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Начинаем с конца списка
        binding.messagesRv.setLayoutManager(layoutManager);
        messagesAdapter = new MessagesAdapter(messages);
        binding.messagesRv.setAdapter(messagesAdapter);
    }

    // Отправка сообщений в Firebase
    private void sendMessage(String chatId, String message, String date) {
        if (chatId == null) return;

        HashMap<String, String> messageInfo = new HashMap<>();
        messageInfo.put("text", message);
        messageInfo.put("ownerId", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        messageInfo.put("date", date);

        FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId)
                .child("messages").push().setValue(messageInfo);
    }

    // Загрузка сообщений из Firebase
    private void loadMessages(String chatId) {
        if (chatId == null) return;

        FirebaseDatabase.getInstance().getReference().child("Chats")
                .child(chatId).child("messages").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) return;

                        messages.clear(); // Очищаем список перед загрузкой новых данных
                        for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                            String messageId = messageSnapshot.getKey();
                            String ownerId = Objects.requireNonNull(messageSnapshot.child("ownerId").getValue()).toString();
                            String text = Objects.requireNonNull(messageSnapshot.child("text").getValue()).toString();
                            String date = Objects.requireNonNull(messageSnapshot.child("date").getValue()).toString();

                            messages.add(new Message(messageId, ownerId, text, date));
                        }
                        messagesAdapter.notifyDataSetChanged(); // Уведомляем адаптер об изменениях
                        binding.messagesRv.scrollToPosition(messagesAdapter.getItemCount() - 1); // Прокручиваем вниз
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("ChatActivity", "Error loading messages: " + error.getMessage());
                    }
                });
    }
}
