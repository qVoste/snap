package com.voste.snap.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import com.voste.snap.databinding.FragmentNewChatBinding;
import com.voste.snap.model.User;
import com.voste.snap.adapter.UsersAdapter;

public class NewChatFragment extends Fragment {
    private FragmentNewChatBinding binding;

    @Nullable
    @Override
    //привязка данных к интерфейсу
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewChatBinding.inflate(inflater, container, false);
        loadUsers();
        return binding.getRoot();
    }
    //загрузка пользователей
    private void loadUsers() {
        ArrayList<User> users = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    //пропуск текущего пользователя
                    if (Objects.equals(userSnapshot.getKey(), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                        continue;
                    }

                    String uid = userSnapshot.getKey();
                    String username = Objects.requireNonNull(userSnapshot.child("username").getValue()).toString();
                    String profileImage = Objects.requireNonNull(userSnapshot.child("profileImage").getValue()).toString();

                    users.add(new User(uid, username, profileImage));
                }
                //менеджер компоновки для RecyclerView
                binding.usersRv.setLayoutManager(new LinearLayoutManager(getContext()));
                //разделители между элементами
                binding.usersRv.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
                //адаптер для RecyclerView
                binding.usersRv.setAdapter(new UsersAdapter(users));
            }
            //обработчик ошибок FireBase
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}