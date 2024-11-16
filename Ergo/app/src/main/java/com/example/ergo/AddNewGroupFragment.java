package com.example.ergo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ergo.model.User;
import com.example.ergo.retrofit.RetrofitService;
import com.example.ergo.retrofit.UserAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewGroupFragment extends Fragment {
    private User user;
    private Button SaveGroupButton;
    private Spinner userSpinner;
    private TextView selectedFriendsText;
    private List<Map<String, Object>> userList = new ArrayList<>();
    private List<String> selectedFriends = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_group_fragment, container, false);

        if (getArguments() != null) {
            user = (User ) getArguments().getSerializable("user");
        }

        SaveGroupButton = view.findViewById(R.id.SaveGroupButton);
        userSpinner = view.findViewById(R.id.userSpinnerGroup);
        selectedFriendsText = view.findViewById(R.id.selectedFriendsText);

        // Load all users initially
        loadAllUsers();

        // Set up the spinner adapter
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(spinnerAdapter);

        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Assuming the first item is a prompt
                    Map<String, Object> selectedUserMap = userList.get(position - 1); // Adjust for prompt
                    String username = (String) selectedUserMap.get("username");
                    if (!selectedFriends.contains(username)) {
                        selectedFriends.add(username);
                        updateSelectedFriendsText();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        SaveGroupButton.setOnClickListener(v -> {
            // Handle saving the group with selected friends
            Toast.makeText(getActivity(), "Group saved with members: " + selectedFriends, Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void loadAllUsers() {
        UserAPI userAPI = new RetrofitService().getRetrofit().create(UserAPI.class);
        userAPI.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    List<String> usernames = new ArrayList<>();
                    usernames.add("Select a friend"); // Prompt for spinner
                    for (User  user : response.body()) {
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("id", user.getId());
                        userMap.put("username", user.getUsername());
                        userList.add(userMap);
                        usernames.add(user.getUsername());
                    }
                    ArrayAdapter<String> spinnerAdapter = (ArrayAdapter<String>) userSpinner.getAdapter();
                    spinnerAdapter.clear();
                    spinnerAdapter.addAll(usernames);
                    spinnerAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "No users found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable throwable) {
                Log.e("API Failure", "Error occurred while loading users", throwable);
                Toast.makeText(getActivity(), "Load failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateSelectedFriendsText() {
        selectedFriendsText.setText("Selected Friends: " + String.join(", ", selectedFriends));
    }
}