package com.example.ergo;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FriendsFragment extends Fragment {
    private Button addNewFriendButton;
    private Button addNewGroupButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_groups_fragment, container, false);

        addNewFriendButton = view.findViewById(R.id.AddNewFriendButton);
        addNewGroupButton = view.findViewById(R.id.AddNewGroupButton);

//        addNewFriendButton.setOnClickListener(v -> ((MainActivity) getActivity()).loadFragment(new AddNewFriendFragment()));
//        addNewGroupButton.setOnClickListener(v -> ((MainActivity) getActivity()).loadFragment(new AddNewGroupFragment()));
//

        return view;
    }
}
