package com.voste.snap.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import com.voste.snap.R;
import com.voste.snap.adapter.ChatsAdapter;
import com.voste.snap.databinding.FragmentChatsBinding;
import com.voste.snap.model.Chat;
import com.voste.snap.utils.ChatCacheHelper;

public class ChatsFragment extends Fragment {
    private FragmentChatsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);

        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        int grayColor = ContextCompat.getColor(requireActivity(), R.color.gray);
        requireActivity().getWindow().setStatusBarColor(grayColor);

        binding.createChatButton.setOnClickListener(v -> {
            NewChatFragment newChatFragment = new NewChatFragment();

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, newChatFragment)
                    .addToBackStack(null)
                    .commit();
        });
        loadChats();

        return binding.getRoot();
    }

    private void loadChats() {
        ArrayList<Chat> chats = new ArrayList<>();
        ChatCacheHelper chatCacheHelper = new ChatCacheHelper(getContext());

        // Попытка загрузить чаты из кеша
        chats = new ArrayList<>(chatCacheHelper.loadChats());

        // Если кеш пуст, загружаем чаты из Firebase
        if (chats.isEmpty()) {
            String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                    ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                    : null;

            if (uid == null) {
                Toast.makeText(getContext(), "User is not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<Chat> finalChats = chats;
            FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
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

                            if (userId1 == null || userId2 == null) continue;

                            String chatUserId = uid.equals(userId2) ? userId2 : userId1;
                            String chatName = snapshot.child("Users").child(chatUserId).child("username").getValue(String.class);

                            if (chatName != null) {
                                Chat chat = new Chat(chatId, chatName, userId1, userId2);
                                finalChats.add(chat);
                            }
                        }
                    }

                    // Сохранение чатов в кеш
                    chatCacheHelper.saveChats(finalChats);

                    binding.chatsRv.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.chatsRv.setAdapter(new ChatsAdapter(finalChats));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to get user chats: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Если чаты найдены в кеше, отображаем их
            binding.chatsRv.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.chatsRv.setAdapter(new ChatsAdapter(chats));
        }
    }

}
