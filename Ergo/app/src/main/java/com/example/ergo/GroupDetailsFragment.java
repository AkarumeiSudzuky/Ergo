package com.example.ergo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ergo.model.Task;
import com.example.ergo.model.Team;
import com.example.ergo.model.User;

import java.util.Set;
import java.util.stream.Collectors;

public class GroupDetailsFragment extends Fragment {
    private Team team;
    private User user;
    private Task task;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_display_fragment, container, false);

        if (getArguments() != null) {
            team = (Team) getArguments().getSerializable("team");
            user = (User) getArguments().getSerializable("user");
            task = (Task) getArguments().getSerializable("task");
        }

        displayGroupData(view);

        return view;
    }


    private void displayGroupData(View view) {
        TextView groupDetailsTextView = view.findViewById(R.id.GroupDetails);
        TextView membersListTextView = view.findViewById(R.id.MembersListTextView);
        RecyclerView teamTaskRecyclerView = view.findViewById(R.id.teamTaskRecyclerView);
        TextView newTasksButton = view.findViewById(R.id.NewTasksButton);

        if (team != null) {
            groupDetailsTextView.setText(team.getName());
            membersListTextView.setText("Members: " + getUsernamesFromTeam(team));
        }


        newTasksButton.setOnClickListener(v -> {
            // Create a new instance of AddTaskFragment
            AddTaskFragment addTaskFragment = new AddTaskFragment();

            // Create a Bundle to pass the group_id
            Bundle bundle = new Bundle();

            // Pass the group_id (assuming team has a method getId())
            if (team != null) {
                long groupId = team.getId();
                bundle.putLong("group_id", groupId); // Pass the team ID as group_id
                Log.d("GroupDetailsFragment", "Passing group_id: " + groupId);
            } else {
                bundle.putLong("group_id", -1); // Pass an invalid ID if team is null
                Log.d("GroupDetailsFragment", "Team is null, passing group_id: -1");
            }

            // Pass the user object if needed
            bundle.putSerializable("user", user);

            // Set the arguments for the fragment
            addTaskFragment.setArguments(bundle);

            // Navigate to AddTaskFragment (assuming you're using FragmentManager)
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, addTaskFragment) // Replace with your container ID
                    .addToBackStack(null)
                    .commit();
        });
    }

    private String getUsernamesFromTeam(Team team) {
        Set<User> userSet = team.getUsers();
        return userSet.stream()
                .map(User::getUsername)
                .collect(Collectors.joining(", "));
    }
}
