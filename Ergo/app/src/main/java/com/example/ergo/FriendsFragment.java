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
import android.widget.Toast;

import com.example.ergo.model.User;
import com.example.ergo.retrofit.RetrofitService;
import com.example.ergo.retrofit.UserAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsFragment extends Fragment {
    private Button addNewFriendButton;
    private Button addNewGroupButton;
    private User user;
    private List<User> friendsList = new ArrayList<>();

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


        loadFriends();

        return view;
    }

    private void loadFriends(){
        UserAPI userAPI = new RetrofitService().getRetrofit().create(UserAPI.class);
        userAPI.getAllFriends(user.getId()).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()&& response.body() != null ) {
                    friendsList = response.body();
                    for (User friend : friendsList) {
                        Log.d("FriendsFragment", "Friend: " + friend.toString());
                    }
                } else {
                    Log.e("FriendsFragment", "Failed to retrieve friends: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable throwable) {
                Log.e("FriendsFragment", "Error loading friends", throwable);
            }
        });
    }
}
