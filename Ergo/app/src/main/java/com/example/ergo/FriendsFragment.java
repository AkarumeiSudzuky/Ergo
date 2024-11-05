package com.example.ergo;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ergo.model.User;

public class FriendsFragment extends Fragment {
    private Button addNewFriendButton;
    private Button addNewGroupButton;
    User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_groups_fragment, container, false);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }

        addNewFriendButton = view.findViewById(R.id.AddNewFriendButton);
        addNewGroupButton = view.findViewById(R.id.AddNewGroupButton);

        addNewFriendButton.setOnClickListener(v -> ((MainActivity) getActivity()).loadFragment(new AddNewFriendFragment(), user));
        addNewGroupButton.setOnClickListener(v -> ((MainActivity) getActivity()).loadFragment(new AddNewGroupFragment(), user));


        return view;
    }
}
