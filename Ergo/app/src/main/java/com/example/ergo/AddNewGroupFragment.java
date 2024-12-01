package com.example.ergo;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ergo.model.Team;
import com.example.ergo.model.User;
import com.example.ergo.retrofit.RetrofitService;
import com.example.ergo.retrofit.TeamApi;
import com.example.ergo.retrofit.UserAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewGroupFragment extends Fragment {
    private User user;
    private Button saveGroupButton;
    private Spinner userSpinner;
    private TextView selectedFriendsText;
    private Set<User> selectedFriends = new HashSet<>();
    private List<User> friendsList = new ArrayList<>();
    private EditText titleEditText;
    private RetrofitService retrofitService = new RetrofitService();
    private TeamApi teamApi = retrofitService.getRetrofit().create(TeamApi.class);

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_group_fragment, container, false);

        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }

        saveGroupButton = view.findViewById(R.id.SaveGroupButton);
        userSpinner = view.findViewById(R.id.userSpinnerGroup);
        selectedFriendsText = view.findViewById(R.id.selectedFriendsText);
        titleEditText = view.findViewById(R.id.TitleET);


        loadFriends();

        // Set up the spinner adapter
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(spinnerAdapter);

        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Assuming the first item is a prompt
                    User selectedUser = friendsList.get(position - 1); // Adjust for prompt
                    if (!selectedFriends.contains(selectedUser)) { // Add User object
                        selectedFriends.add(selectedUser);
                        updateSelectedFriendsText();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        saveGroupButton.setOnClickListener(v -> saveGroupWithUsers());

        setupKeyboardListeners(view);

        return view;
    }

    private void loadFriends() {
        UserAPI userAPI = new RetrofitService().getRetrofit().create(UserAPI.class);
        userAPI.getAllFriends(user.getId()).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    friendsList = response.body();
                    List<String> usernames = new ArrayList<>();
                    usernames.add("Select a friend");

                    for (User friend : friendsList) {
                        usernames.add(friend.getUsername());
                    }

                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, usernames);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    userSpinner.setAdapter(spinnerAdapter);
                } else {
                    Toast.makeText(getActivity(), "Failed to load friends", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable throwable) {
                Toast.makeText(getActivity(), "Error loading friends", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSelectedFriendsText() {
        List<String> usernames = new ArrayList<>();
        for (User user : selectedFriends) {
            usernames.add(user.getUsername());
        }
        selectedFriendsText.setText("Selected Friends: " + String.join(", ", usernames));
    }

    private void saveGroupWithUsers() {
        String title = titleEditText.getText().toString().trim();
        if (title.isEmpty() || selectedFriends.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        Team team = new Team();
        team.setName(title);

        teamApi.saveTeam(team).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Team saved successfully!", Toast.LENGTH_SHORT).show();
                    findLastTeam();
                } else {
                    Log.e("API Error", "Failed to save team: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Log.e("API Failure", "Error occurred", throwable);
                Toast.makeText(getActivity(), "Save team failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void findLastTeam() {
        teamApi.getLastTeamId().enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Long lastTeamId = response.body();
                    Long newTeamId = lastTeamId;
                    Log.d("LastTeamId", "The last team ID is: " + lastTeamId + ", new team ID: " + newTeamId);
                    // Proceed with the new team ID, for example, add users to the new team
                    addUsersToTeam(newTeamId, selectedFriends);
                } else {
                    // If there is no team in the database, the response body will be null
                    Log.d("LastTeamId", "No teams found, setting ID to 1");
                    // Proceed with ID 1 as a new starting point
//                    addUsersToTeam(1L, selectedFriends);
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable throwable) {
                Log.e("API Failure", "Error occurred", throwable);
                Toast.makeText(getActivity(), "Failed to fetch last team ID", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void addUsersToTeam(Long teamId, Set<User> users) {
        users.add(user);
        teamApi.addUsersToTeam(teamId, users).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Members added successfully!", Toast.LENGTH_LONG).show();
                } else {
                    Log.e("API Error", "Failed to add users: " + response.message());
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("API Error", "Error body: " + errorBody);
                    } catch (IOException e) {
                        Log.e("API Error", "Failed to read error body", e);
                    }
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Log.e("API Failure", "Error occurred", throwable);
                Toast.makeText(getActivity(), "Failed to add users!", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void setupKeyboardListeners(View view) {
        MainActivity mainActivity = (MainActivity) getActivity();

        // For EditText
        titleEditText.setOnFocusChangeListener((view1, hasFocus) -> {
            if (mainActivity != null) {
                mainActivity.setBottomNavigationVisibility(!hasFocus);
            }
        });

        // Optionally, add a TextWatcher to hide the BottomNavigationView while typing
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mainActivity != null) {
                    mainActivity.setBottomNavigationVisibility(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Add the global layout listener to detect keyboard visibility
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                view.getWindowVisibleDisplayFrame(r);
                int heightDiff = view.getRootView().getHeight() - (r.bottom - r.top);

                // If more than 200 pixels, its probably a keyboard...
                if (heightDiff > 200) {
                    // Keyboard is visible
                    if (mainActivity != null) {
                        mainActivity.setBottomNavigationVisibility(false);
                    }
                } else {
                    // Keyboard is hidden
                    if (mainActivity != null) {
                        mainActivity.setBottomNavigationVisibility(true);
                    }
                }
            }
        });
    }
}

