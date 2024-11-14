package com.example.ergo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.ergo.model.User;

import java.util.ArrayList;

public class TasksPagerAdapter extends FragmentPagerAdapter {
    private User user;  // Store the user object
    private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private final ArrayList<String> fragmentTitle = new ArrayList<>();

    // Accept user in the constructor
    public TasksPagerAdapter(@NonNull FragmentManager fm, int behavior, User user) {
        super(fm, behavior);
        this.user = user;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragmentArrayList.get(position);

//        // Pass the user to the fragments when required
//        if (fragment instanceof CurrentTasksFragment) {
//            ((CurrentTasksFragment) fragment).setUser(user);
//        } else if (fragment instanceof CompletedTasksFragment) {
//            ((CompletedTasksFragment) fragment).setUser(user);
//        }

        return fragment;
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentArrayList.add(fragment);
        fragmentTitle.add(title);
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        fragment.setArguments(bundle);


    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitle.get(position);
    }
}
