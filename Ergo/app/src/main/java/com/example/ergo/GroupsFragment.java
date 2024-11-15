package com.example.ergo;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
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

public class GroupsFragment extends Fragment {
    private Button addNewGroupButton;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.groups_fragment, container, false);

        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }

        addNewGroupButton = view.findViewById(R.id.AddNewGroupButton);

        addNewGroupButton.setOnClickListener(v -> ((MainActivity) getActivity()).loadFragment(new AddNewGroupFragment(), user));

//        fetchFriendsList();

        return view;
    }
//
//    private void fetchFriendsList() {
//        RetrofitService retrofitService = new RetrofitService();
//        UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
//
//        Call<List<User>> call = userAPI.getAllFriends(user.getId());  // Adjust the ID parameter if necessary
//        call.enqueue(new Callback<List<User>>() {
//            @Override
//            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    friendsList = response.body();
//                    // Use an ArrayAdapter to bind data to the ListView
//                    FriendsAdapter adapter = new FriendsAdapter(getContext(), friendsList);
//                    friendsListView.setAdapter(adapter);
//                } else {
//                    Toast.makeText(getContext(), "Failed to fetch friends", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<User>> call, Throwable t) {
//                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    public class FriendsAdapter extends ArrayAdapter<User> {
//        private Context context;
//        private List<User> friends;
//
//        public FriendsAdapter(Context context, List<User> friends) {
//            super(context, R.layout.friend_list_item, friends);
//            this.context = context;
//            this.friends = friends;
//        }
//
//        @NonNull
//        @Override
//        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
//            if (convertView == null) {
//                convertView = LayoutInflater.from(context).inflate(R.layout.friend_list_item, parent, false);
//            }
//
//            User friend = friends.get(position);
//
//            TextView friendNameTextView = convertView.findViewById(R.id.Friend_list_name);
//            TextView friendGroupsTextView = convertView.findViewById(R.id.Friend_list_item_in_groups);
//
//            friendNameTextView.setText(friend.getUsername()); // Assuming User model has a getUsername() method
//            friendGroupsTextView.setText("In 0 groups with you"); // Replace this with actual data if available
//
//            convertView.setOnClickListener(v -> showPopupMenu(v, friend));
//
//            return convertView;
//        }
//
//        private void showPopupMenu(View v, User friend) {
//            PopupMenu popupMenu = new PopupMenu(context, v);
//            MenuInflater inflater = popupMenu.getMenuInflater();
//            inflater.inflate(R.menu.friend_options_menu, popupMenu.getMenu());
//
//            popupMenu.setOnMenuItemClickListener(item -> {
//                if (item.getItemId() == R.id.delete_friend) {
//                    showDeleteConfirmationDialog(friend);
//                    return true;
//                }
//                return false;
//            });
//
//            popupMenu.show();
//        }
//
//        private void showDeleteConfirmationDialog(User friend) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setTitle("Delete Friend")
//                    .setMessage("Are you sure you want to delete " + friend.getUsername() + " from your friends list?")
//                    .setPositiveButton("Yes", (dialog, which) -> {
//                        deleteFriend(friend);
//                    })
//                    .setNegativeButton("No", null)
//                    .show();
//        }
//
//        private void deleteFriend(User friend) {
//            RetrofitService retrofitService = new RetrofitService();
//            UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
//
//            // Call the removeFriend API
//            Call<Void> call = userAPI.removeFriend(user.getId(), friend.getId());  // Adjust if necessary
//            call.enqueue(new Callback<Void>() {
//                @Override
//                public void onResponse(Call<Void> call, Response<Void> response) {
//                    if (response.isSuccessful()) {
//                        Toast.makeText(context, friend.getUsername() + " has been deleted.", Toast.LENGTH_SHORT).show();
//                        friends.remove(friend);
//                        notifyDataSetChanged();
//                    } else {
//                        Toast.makeText(context, "Failed to delete friend", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Void> call, Throwable t) {
//                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//
//
//
//    }
}
