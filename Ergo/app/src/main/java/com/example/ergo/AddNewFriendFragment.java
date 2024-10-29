package com.example.ergo;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AddNewFriendFragment extends Fragment {
    private Button addNewFriend;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_friend_fragment, container, false);

        addNewFriend = view.findViewById(R.id.AddNewFriendButton);
        //actual performAddFriend() somewhere here:
//        addNewFriend.setOnClickListener(...);
//        addNewFriend.setOnClickListener(v -> ((MainActivity) getActivity()).loadFragment(new FriendsFragment()));


        return view;


    }
}
