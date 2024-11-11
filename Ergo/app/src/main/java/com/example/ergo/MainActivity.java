package com.example.ergo;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ergo.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private User activeUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
//        setupKeyboardVisibilityListener();

        // Load the LoginFragment by default, tasks in other cases
        if (savedInstanceState == null) {
            loadFragment(new LoginFragment(), activeUser);
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
            loadFragment(selectedFragment, activeUser);  // Pass activeUser to the fragment
        }
        return true;
    }


    public void onLoginSuccess(User user) {
        activeUser = user;
        loadFragment(new TasksFragment(), user);
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    public void logOut() {
        // Hide the BottomNavigationView after logout
        bottomNavigationView.setVisibility(View.GONE);

        // Clear the active user
        activeUser = null;

        // Reload the LoginFragment
        loadFragment(new LoginFragment(), activeUser);
    }

    protected void loadFragment(Fragment fragment, User user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }


//    private void setupKeyboardVisibilityListener() {
//        final View rootView = findViewById(android.R.id.content);
//        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                Rect r = new Rect();
//                rootView.getWindowVisibleDisplayFrame(r);
//                int heightDiff = rootView.getRootView().getHeight() - (r.bottom - r.top);
//                // Check if the height difference is more than a threshold
//                if (heightDiff > 200) { // 200 can be adjusted as per your requirement
//                    // Keyboard is opened
//                    bottomNavigationView.setVisibility(View.GONE);
//                } else {
//                    // Keyboard is closed
//                    bottomNavigationView.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//    }
}