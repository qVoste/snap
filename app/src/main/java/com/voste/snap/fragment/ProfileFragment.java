package com.voste.snap.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.Objects;

import com.voste.snap.R;
import com.voste.snap.ui.LoginActivity;
import com.voste.snap.databinding.FragmentProfileBinding;
import com.voste.snap.ui.MainActivity;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private Uri filePath;
    private ActivityResultLauncher<Intent> pickImageActivityResultLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requireActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        requireActivity().getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);

        pickImageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                            filePath = result.getData().getData();

                            try {
                                // Преобразование изображения в Bitmap
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                        requireContext().getContentResolver(),
                                        filePath
                                );
                                binding.profileImageView.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                            }

                            // Загрузка изображения на сервер
                            uploadImage();
                        }
                    }
                }
        );
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Сброс полноэкранного режима
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        // Загрузка данных пользователя
        loadUserInfo();

        // Обработчик выбора изображения профиля
        binding.avatar.setOnClickListener(v -> selectImage());

        // Выход из аккаунта
        binding.backAccountButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
            requireActivity().finish(); // Закрытие текущей активности
        });

        binding.backButton.setOnClickListener(v -> {
            // Замена текущего фрагмента на ChatsFragment
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ChatsFragment()) // Заменить текущий фрагмент
                    .commit();
        });


        return binding.getRoot();
    }

    // Загрузка информации о пользователе из Firebase
    private void loadUserInfo() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(requireContext(), "User is not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Получаем URL последнего известного изображения из SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("SnapPrefs", Context.MODE_PRIVATE);
        String cachedProfileImage = prefs.getString("lastProfileImage", null);

        // Если есть кэшированное изображение, показываем его
        if (cachedProfileImage != null && !cachedProfileImage.isEmpty()) {
            Glide.with(requireContext())
                    .load(cachedProfileImage) // Загружаем кэшированное изображение
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Полное кэширование
                    .placeholder(R.drawable.icon_default_avatar) // Заглушка
                    .into(binding.profileImageView);
        } else {
            // Если нет кэшированного изображения, показываем заглушку
            Glide.with(requireContext())
                    .load(R.drawable.icon_default_avatar) // Заглушка
                    .into(binding.profileImageView);
        }

        // Асинхронно подгружаем изображение из Firebase
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String profileImage = snapshot.child("profileImage").getValue(String.class);
                        String name = snapshot.child("username").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);

                        if (profileImage != null && !profileImage.isEmpty()) {
                            Glide.with(requireContext())
                                    .load(profileImage)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .placeholder(R.drawable.icon_default_avatar)
                                    .into(binding.profileImageView);
                            prefs.edit().putString("lastProfileImage", profileImage).apply();
                        }

                        if (name != null && !name.isEmpty()) {
                            binding.usernameTv.setText(name);  // Обновить имя
                            prefs.edit().putString("lastProfileName", name).apply(); // Кэширование имени
                        }

                        if (email != null && !email.isEmpty()) {
                            binding.emailButtonText.setText(email);  // Обновить имя
                            prefs.edit().putString("lastProfileEmail", email).apply(); // Кэширование имени
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), "Failed to load user info: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }




    // Открытие выбора изображения для профиля
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImageActivityResultLauncher.launch(intent);
    }

    // Загрузка изображения на Firebase Storage
    private void uploadImage() {
        if (filePath != null) {
            String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            FirebaseStorage.getInstance().getReference().child("images/" + uid)
                    .putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Перед обращением к контексту проверяем, привязан ли фрагмент
                        if (isAdded()) {
                            FirebaseStorage.getInstance().getReference().child("images/" + uid)
                                    .getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                                                .child("profileImage").setValue(uri.toString());
                                        Toast.makeText(requireContext(), "Photo upload complete", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "Failed to upload photo", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            if (isAdded()) {
                Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
