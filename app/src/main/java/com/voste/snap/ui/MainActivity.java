package com.voste.snap.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.voste.snap.R;
import com.voste.snap.databinding.ActivityMainBinding;
import com.voste.snap.fragment.ChatsFragment;
import com.voste.snap.fragment.ProfileFragment;
import com.voste.snap.fragment.SettingFragment;
import com.voste.snap.fragment.SupportFragment;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Map<Integer, Fragment> fragmentMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.voste.snap.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, ChoiceActivity.class));
            finish();
            return;
        }

        drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navigationView;

        fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.chats, new ChatsFragment());
        fragmentMap.put(R.id.profile, new ProfileFragment());
        fragmentMap.put(R.id.setting, new SettingFragment());
        fragmentMap.put(R.id.support, new SupportFragment());

        if (savedInstanceState == null) {
            loadFragment(R.id.chats);
            navigationView.setCheckedItem(R.id.chats);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            loadFragment(item.getItemId());
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void loadFragment(int menuItemId) {
        Fragment fragment = fragmentMap.get(menuItemId);
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
