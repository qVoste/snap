package com.voste.snap.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.voste.snap.R;
import com.voste.snap.model.User;
import com.voste.snap.utils.ChatUtil;

import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UserViewHolder> { //более глубокая настройка списка пользователей

    private final ArrayList<User> users;

    public UsersAdapter(ArrayList<User> users){
        this.users = users;
    }

    @NonNull
    @Override
    //список пользователей
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_item_rv, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    //привязка данных к RecyclerView
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.username_tv.setText(user.username);

        if (!user.profileImage.isEmpty()){
            Glide.with(holder.itemView.getContext()).load(user.profileImage).into(holder.profileImage_iv);
        }
        //создание чата
        holder.itemView.setOnClickListener(view -> ChatUtil.createChat(user));
    }
    //кол-во пользователей
    @Override
    public int getItemCount() {
        return users.size();
    }
}
