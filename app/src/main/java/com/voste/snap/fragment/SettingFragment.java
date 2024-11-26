package com.voste.snap.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.voste.snap.databinding.FragmentSettingBinding;

public class SettingFragment extends Fragment {
    private FragmentSettingBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Инициализация binding
        binding = FragmentSettingBinding.inflate(inflater, container, false);

        // Настройка интерфейса
        setupUI();

        return binding.getRoot();
    }

    private void setupUI() {
        // Установка текста для TextView (если требуется динамическое изменение)
        binding.text.setText("В РАЗРАБОТКЕ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Предотвращение утечек памяти
    }
}
