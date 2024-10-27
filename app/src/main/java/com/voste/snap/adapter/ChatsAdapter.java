package com.voste.snap.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import com.voste.snap.R;
import com.voste.snap.model.Chat;
import com.voste.snap.ui.ChatActivity;

import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.Objects;

public class ChatsAdapter extends RecyclerView.Adapter<ChatViewHolder> { //более глубокая настройка списка чатов

    private final ArrayList<Chat> chats;

    public ChatsAdapter(ArrayList<Chat> chats){
        this.chats = chats;
    }

    @NonNull
    @Override
    //список чатов
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_item_rv, parent, false);
        return new ChatViewHolder(view);
    }
    //привязка данных к RecyclerView
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.chat_name_tv.setText(chats.get(position).getChat_name());
        String userId;
        //проверка не текущий ли пользователь
        if (!chats.get(position).getUserId1().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
            userId = chats.get(position).getUserId1();
        }else{
            userId = chats.get(position).getUserId2();
        }
        //получаем ссылку на данные профиля
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
                .child("profileImage").get()
                .addOnCompleteListener(task -> {
                    try{
                        String profileImageUrl = Objects.requireNonNull(task.getResult().getValue()).toString();
                        if (!profileImageUrl.isEmpty())
                            Glide.with(holder.itemView.getContext()).load(profileImageUrl).into(holder.chat_iv);
                    }catch(Exception e){
                        Toast.makeText(holder.itemView.getContext(), "Failed to get profile image link", Toast.LENGTH_SHORT).show();
                    }
                });
        //переход в чат
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), ChatActivity.class);
            intent.putExtra("chatId", chats.get(position).getChat_id());
            holder.itemView.getContext().startActivity(intent);
        });
    }
    //кол-во чатов
    @Override
    public int getItemCount() {
        return chats.size();
    }
}
