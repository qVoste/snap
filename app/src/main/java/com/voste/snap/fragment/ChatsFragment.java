package com.voste.snap.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import com.voste.snap.model.Chat;
import com.voste.snap.adapter.ChatsAdapter;
import com.voste.snap.databinding.FragmentChatsBinding;

public class ChatsFragment extends Fragment {
    private FragmentChatsBinding binding;

    @Nullable
    @Override
    //привязка данных к интерфейсу
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        loadChats();
        return binding.getRoot();
    }

    private void loadChats() {
        ArrayList<Chat> chats = new ArrayList<>();

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
        //проверка авторизован ли пользователь
        if (uid == null) {
            Toast.makeText(getContext(), "User is not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Проверка наличия данных
                if (!snapshot.exists() || !snapshot.child("Users").child(uid).child("chats").exists()) {
                    Toast.makeText(getContext(), "No chats found", Toast.LENGTH_SHORT).show();
                    return;
                }

                String chatsStr = snapshot.child("Users").child(uid).child("chats").getValue(String.class);
                if (chatsStr == null || chatsStr.isEmpty()) {
                    Toast.makeText(getContext(), "No chats available", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] chatsIds = chatsStr.split(",");

                for (String chatId : chatsIds) {
                    DataSnapshot chatSnapshot = snapshot.child("Chats").child(chatId);

                    if (chatSnapshot.exists()) {
                        String userId1 = chatSnapshot.child("user1").getValue(String.class);
                        String userId2 = chatSnapshot.child("user2").getValue(String.class);

                        if (userId1 == null || userId2 == null) {
                            continue; // Пропустить, если данные отсутствуют
                        }

                        String chatUserId = uid.equals(userId1) ? userId2 : userId1;
                        String chatName = snapshot.child("Users").child(chatUserId).child("username").getValue(String.class);

                        if (chatName != null) {
                            Chat chat = new Chat(chatId, chatName, userId1, userId2);
                            chats.add(chat);
                        }
                    }
                }
                // Установка адаптера для RecyclerView
                binding.chatsRv.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.chatsRv.setAdapter(new ChatsAdapter(chats));
            }

            @Override
            //обработчик для ошибок из FireBase
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to get user chats: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
