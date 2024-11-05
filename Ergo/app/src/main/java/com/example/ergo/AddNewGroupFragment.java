package com.example.ergo;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.ergo.model.User;
import com.example.ergo.retrofit.RetrofitService;
import com.example.ergo.retrofit.UserAPI;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewGroupFragment extends Fragment {
    private User user;
    private Button SaveGroupButton;

    //searching for users
    private SearchView userSearchView;
    private ListView userListView;
    private ArrayAdapter<User> userAdapter;
    private List<User> userList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_group_fragment, container, false);

        // Retrieve the User object from the arguments
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }

        SaveGroupButton = view.findViewById(R.id.SaveGroupButton);
        userSearchView = view.findViewById(R.id.userSearchViewGroup);
        userListView = view.findViewById(R.id.userListViewGroup);

        userAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
                userList);
        userListView.setAdapter(userAdapter);
        userSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        //SaveGroupButton.setOnClickListener(v -> performSavingGroup());

        return view;
    }

    private void searchUsers(String query) {
        UserAPI userAPI = new RetrofitService().getRetrofit().create(UserAPI.class);
        userAPI.searchUsers(query).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear(); // Clear previous results
                    userList.addAll(response.body()); // Add new results
                    userAdapter.notifyDataSetChanged(); // Notify adapter of data change
                } else {
                    Toast.makeText(getActivity(), "No users found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable throwable) {
                Log.e("API Failure", "Error occurred while searching users", throwable);
                Toast.makeText(getActivity(), "Search failed!", Toast.LENGTH_LONG).show();
            }
        });
    }
}



