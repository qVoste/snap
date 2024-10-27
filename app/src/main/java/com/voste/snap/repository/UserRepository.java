package com.voste.snap.repository;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserRepository {
    private DatabaseReference usersRef;

    public UserRepository() {
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    public void findUserByNickname(String nickname, FirebaseCallback callback) {
        usersRef.orderByChild("username").equalTo(nickname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey(); // Получаем UID пользователя
                        callback.onCallback(userId); // Возвращаем UID через callback
                    }
                } else {
                    callback.onCallback(null); // Пользователь не найден
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DB_ERROR", "Ошибка поиска пользователя: " + databaseError.getMessage());
            }
        });
    }

    public interface FirebaseCallback {
        void onCallback(String userId);
    }
}
