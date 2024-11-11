package com.example.ergo;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ergo.retrofit.RetrofitService;

import com.example.ergo.model.User;
import com.example.ergo.retrofit.UserAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewFriendFragment extends Fragment {
    private Button addNewFriend;
    private User user;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_friend_fragment, container, false);
        // Retrieve the User object from the arguments
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }
        addNewFriend = view.findViewById(R.id.AddNewFriendButton);
        //actual performAddFriend() somewhere here:
//        addNewFriend.setOnClickListener(...);
//        addNewFriend.setOnClickListener(v -> ((MainActivity) getActivity()).loadFragment(new FriendsFragment()));


        return view;


    }



}