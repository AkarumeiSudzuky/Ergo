package com.example.ergo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load the LoginFragment by default
        if (savedInstanceState == null) {
            loadFragment(new LoginFragment());
        }
    }

    protected void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment) // Ensure you have a FrameLayout in your activity_main.xml with this id
                .commit();
    }

    // Optionally, you can add methods to switch between fragments based on user actions
}
