package com.example.ergo;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.ergo.model.User;
import com.google.android.material.tabs.TabLayout;




public class TasksFragment extends Fragment {
    private User user; // Make sure you have the user object here
    private ImageButton logOutButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        // Get the user object passed from the previous fragment or activity
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }
        if (user != null) {
            Toast.makeText(getActivity(), "User ID: " + user.getId(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "User not found!", Toast.LENGTH_LONG).show();
        }

        // Initialize TabLayout and ViewPager
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        tabLayout.setupWithViewPager(viewPager);



        // Pass the user object to the TasksPagerAdapter
        TasksPagerAdapter tasksPageAdapter = new TasksPagerAdapter(getActivity().getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, user);

        tasksPageAdapter.addFragment(new CurrentTasksFragment(), "Current");
        tasksPageAdapter.addFragment(new CompletedTasksFragment(), "Completed");
        viewPager.setAdapter(tasksPageAdapter);

        logOutButton = view.findViewById(R.id.imageButton);
        logOutButton.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.logOut(); // Call the logOut method in MainActivity
            }
        });

        return view;
    }
}
