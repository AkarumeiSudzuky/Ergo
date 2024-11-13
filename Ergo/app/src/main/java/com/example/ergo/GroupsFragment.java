package com.example.ergo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.ergo.model.User;

public class GroupsFragment extends Fragment  {
    private User user;
    
    ////TO-DO add groups


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
    ///actually
        // View view = inflater.inflate(R.layout.fragment_groups, container, false);

        // Get the user object passed from the previous fragment or activity
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }


        return view;
    }
}
