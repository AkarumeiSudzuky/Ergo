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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
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

    // Searching for users
    private SearchView userSearchView;
    private ListView userListView;
    private ArrayAdapter<Map<String, Object>> userAdapter;
    private List<Map<String, Object>> userList = new ArrayList<>();
    private List<Map<String, Object>> filteredList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_group_fragment, container, false);

        // Retrieve the User object from the arguments
        if (getArguments() != null) {
            user = (User ) getArguments().getSerializable("user");
        }

        SaveGroupButton = view.findViewById(R.id.SaveGroupButton);
        userSearchView = view.findViewById(R.id.userSearchViewGroup);
        userListView = view.findViewById(R.id.userListViewGroup);

        // Set up the adapter for displaying users
        userAdapter = new ArrayAdapter<Map<String, Object>>(getActivity(), android.R.layout.simple_list_item_1, userList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                // Inflate the view if it does not exist
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                }

                // Get the current user map
                Map<String, Object> currentUser  = getItem(position);
                String username = (String) currentUser .get("username");

                // Set the username in the TextView
                TextView textView = convertView.findViewById(android.R.id.text1);
                textView.setText(username);

                return convertView;
            }
        };

        userListView.setAdapter(userAdapter);

        // Set up the search view to filter users
        userSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUsers(newText);
                return true;
            }
        });

        // Load all users initially
        loadAllUsers();

        // Set up the item click listener for the user list
        userListView.setOnItemClickListener((parent, view1, position, id) -> {
            Map<String, Object> selectedUserMap = userAdapter.getItem(position);

            Long userId = (Long) selectedUserMap.get("id");
            String username = (String) selectedUserMap.get("username");
            Toast.makeText(getActivity(), "Selected: " + username + " (ID: " + userId + ")", Toast.LENGTH_SHORT).show();
            // Handle the selected user here (e.g., add to group)
        });

        return view;
    }

    private void loadAllUsers() {
        UserAPI userAPI = new RetrofitService().getRetrofit().create(UserAPI.class);
        userAPI.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response .body() != null) {
                    userList.clear();
                    for (User  user : response.body()) {
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("id", user.getId());
                        userMap.put("username", user.getUsername());
                        userList.add(userMap);
                    }
                    userAdapter.notifyDataSetChanged();
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

    private void filterUsers(String query) {
        filteredList.clear();
        for (Map<String, Object> userMap : userList) {
            String username = (String) userMap.get("username");
            if (username.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(userMap);
            }
        }
        userAdapter.clear();
        userAdapter.addAll(filteredList);
        userAdapter.notifyDataSetChanged();
        userListView.setVisibility(filteredList.isEmpty() ? View.GONE : View.VISIBLE);
    }
}