package com.example.ergo;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
    private ListView friendsListView;
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
        friendsListView = view.findViewById(R.id.FriendListView);

        addNewFriendButton.setOnClickListener(v -> ((MainActivity) getActivity()).loadFragment(new AddNewFriendFragment(), user));
        addNewGroupButton.setOnClickListener(v -> ((MainActivity) getActivity()).loadFragment(new AddNewGroupFragment(), user));

        fetchFriendsList();

        return view;
    }

    private void fetchFriendsList() {
        RetrofitService retrofitService = new RetrofitService();
        UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);

        Call<List<User>> call = userAPI.getAllFriends(user.getId());  // Adjust the ID parameter if necessary
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    friendsList = response.body();
                    // Use an ArrayAdapter to bind data to the ListView
                    FriendsAdapter adapter = new FriendsAdapter(getContext(), friendsList);
                    friendsListView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Failed to fetch friends", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class FriendsAdapter extends ArrayAdapter<User> {
        private Context context;
        private List<User> friends;

        public FriendsAdapter(Context context, List<User> friends) {
            super(context, R.layout.friend_list_item, friends);
            this.context = context;
            this.friends = friends;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.friend_list_item, parent, false);
            }

            User friend = friends.get(position);

            TextView friendNameTextView = convertView.findViewById(R.id.Friend_list_name);
            TextView friendGroupsTextView = convertView.findViewById(R.id.Friend_list_item_in_groups);

            friendNameTextView.setText(friend.getUsername()); // Assuming User model has a getUsername() method
            friendGroupsTextView.setText("In 0 groups with you"); // Replace this with actual data if available

            return convertView;
        }
    }
}
