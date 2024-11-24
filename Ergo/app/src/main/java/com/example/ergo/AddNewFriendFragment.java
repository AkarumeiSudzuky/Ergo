package com.example.ergo;

import static android.widget.Toast.LENGTH_LONG;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ergo.retrofit.RetrofitService;

import com.example.ergo.model.User;
import com.example.ergo.retrofit.UserAPI;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewFriendFragment extends Fragment {
    private Button addNewFriend;
    private EditText friendUsernameET;
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
        friendUsernameET = view.findViewById(R.id.GivenFriendNameET);


        //actual performAddFriend() somewhere here:
        addNewFriend.setOnClickListener(v -> performAddFriend());
//        addNewFriend.setOnClickListener(v -> ((MainActivity) getActivity()).loadFragment(new FriendsFragment()));


        return view;
    }


    private void performAddFriend() {
        String friendUsername = friendUsernameET.getText().toString().trim();

        if (friendUsername.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter a username", LENGTH_LONG).show();
            return;
        }

        RetrofitService retrofitService = new RetrofitService();
        UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);

        if (user == null) {
            Log.e("AddNewFriendFragment", "Current user is null");
            Toast.makeText(getActivity(), "Error: user not found!", LENGTH_LONG).show();
            return;
        }

        Long currentUserId = user.getId();

        // Fetch the friend's ID based on the entered username
        fetchFriendByUsername(userAPI, friendUsername, new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Long friendId = response.body().getId();
                    addFriend(userAPI, currentUserId, friendId);
                    Log.d("AddNewFriend", "Friend found and added successfully");
                } else {
                    Toast.makeText(getActivity(), "Friend not found!", LENGTH_LONG).show();
                    Log.e("API Error", "Failed to find friend. Code: " + response.code() + ", Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                Log.e("API Failure", "Error fetching friend by username", throwable);
                // You can add a specific message here to help identify the problem:
                if (throwable instanceof IOException) {
                    // Network issues
                    Toast.makeText(getActivity(), "Network error. Please check your connection.", LENGTH_LONG).show();
                } else {
                    // Other failures
                    Toast.makeText(getActivity(), "Error fetching friend!", LENGTH_LONG).show();
                }
            }
        });
    }

    private void fetchFriendByUsername(UserAPI userAPI, String username, Callback<User> callback) {
        Call<User> call = userAPI.getUserByUsername(username);
        call.enqueue(callback);
    }

    private void addFriend(UserAPI userAPI, Long userId, Long friendId) {
        userAPI.addFriend(userId, friendId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // If the response body is empty, show a success message
                    Toast.makeText(getActivity(), "Friend added successfully!", LENGTH_LONG).show();
                } else {
                    Log.e("API Error", "Failed to add friend. Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(getActivity(), "Failed to add friend. Try again!", LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Log.e("API Failure in addFriend", "Error adding friend", throwable);
                Toast.makeText(getActivity(), "Failed to add friend!", LENGTH_LONG).show();
            }
        });
    }
}