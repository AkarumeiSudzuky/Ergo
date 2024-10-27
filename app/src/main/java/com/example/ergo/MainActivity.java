package com.example.ergo;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Load the LoginFragment by default, tasks in other cases
        if (savedInstanceState == null) {
            loadFragment(new LoginFragment());
            bottomNavigationView.setVisibility(View.GONE);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

        private boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.tasks_tab_item) {
                selectedFragment = new TasksFragment();
            } else if (id == R.id.new_task) {
                selectedFragment = new AddTaskFragment();
            } else if (id == R.id.friends) {
                selectedFragment = new FriendsFragment();
            }


            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;

    }

    protected void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }


    public void onLoginSuccess() {
        loadFragment(new TasksFragment());
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

}
