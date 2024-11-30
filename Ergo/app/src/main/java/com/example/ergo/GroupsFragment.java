package com.example.ergo;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ergo.model.Team;
import com.example.ergo.model.User;
import com.example.ergo.retrofit.RetrofitService;
import com.example.ergo.retrofit.TeamApi;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("deprecation")
public class GroupsFragment extends Fragment {
    private User user;
    private Button addNewGroupButton;
    private ListView groupsListView;
    private TeamApi teamApi;
    private List<Team> teamList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            MainActivity activity = (MainActivity) context;
            activity.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    // Handle back press, but prevent default swipe behavior
                }
            });
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.groups_fragment, container, false);
        initializeViews(view);
        retrieveUserFromArguments();
        setupRetrofitService();
        setupAddNewGroupButton();

        if (user != null) {
            fetchTeamsList();
        } else {
            showToast("User  data is missing");
        }


        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                view.getWindowVisibleDisplayFrame(r);
                int heightDiff = view.getRootView().getHeight() - (r.bottom - r.top);

                if (heightDiff > 200) {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).setBottomNavigationVisibility(false);
                    }
                } else {
                    // Keyboard is hidden
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).setBottomNavigationVisibility(true);
                    }
                }
            }
        });

        return view;
    }

    private void initializeViews(View view) {
        addNewGroupButton = view.findViewById(R.id.AddNewGroupButton);
        groupsListView = view.findViewById(R.id.groupsListView);
    }

    private void retrieveUserFromArguments() {
        if (getArguments() != null) {
            user = (User ) getArguments().getParcelable("user");
        }
    }

    private void setupRetrofitService() {
        RetrofitService retrofitService = new RetrofitService();
        teamApi = retrofitService.getRetrofit().create(TeamApi.class);
    }

    private void setupAddNewGroupButton() {
        addNewGroupButton.setOnClickListener(v ->
                loadFragment(new AddNewGroupFragment(), user)
        );
    }

    private void fetchTeamsList() {
        teamApi.getAllTeamsForUser (user.getId()).enqueue(new Callback<List<Team>>() {
            @Override
            public void onResponse(Call<List<Team>> call, Response<List<Team>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    teamList = response.body();
                    setupGroupsAdapter();
                } else {

                    showToast("No teams found");
                }
            }

            @Override
            public void onFailure(Call<List<Team>> call, Throwable throwable) {
                showToast("Error loading teams: " + throwable.getMessage());
            }
        });
    }

    private void setupGroupsAdapter() {
        GroupsAdapter adapter = new GroupsAdapter(getActivity(), teamList, user);
        groupsListView.setAdapter(adapter);
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void loadFragment(Fragment fragment, User user) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        fragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null) // Optional: add to back stack for navigation
                .commit();
    }

    public class GroupsAdapter extends ArrayAdapter<Team> {
        private Context context;
        private List<Team> groups;
        private User user; // Store User reference

        public GroupsAdapter(Context context, List<Team> groups, User user) {
            super(context, R.layout.group_list_item, groups);
            this.context = context;
            this.groups = groups;
            this.user = user; // Store the user reference
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.group_list_item, parent, false);
            }

            Team team = groups.get(position);
            bindTeamDataToView(convertView, team);

            convertView.setOnClickListener(v -> {
                // Create a new instance of GroupDetailsFragment
                GroupDetailsFragment groupDetailsFragment = new GroupDetailsFragment();

                // Pass the Team and User objects directly to GroupDetailsFragment
                Bundle bundle = new Bundle();
                bundle.putSerializable("team", team); // Pass the Team object
                bundle.putParcelable("user", user); // Pass the User object
                groupDetailsFragment.setArguments(bundle);

                // Load the GroupDetails Fragment directly from GroupsFragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, groupDetailsFragment)
                        .addToBackStack(null) // Optional: add to back stack for navigation
                        .commit();
            });

            return convertView;
        }

        private void bindTeamDataToView(View convertView, Team team) {
            TextView groupNameTextView = convertView.findViewById(R.id.groupNameTV);
            groupNameTextView.setText(team.getName());

            TextView taskDueTotalTextView = convertView.findViewById(R.id.TaskDueTotal);
            taskDueTotalTextView.setText("0 Tasks Due Total");

            TextView taskDueTodayTextView = convertView.findViewById(R.id.TaskDueToday);
            taskDueTodayTextView.setText("0 Tasks Due Today");

            TextView users = convertView.findViewById(R.id.friends_in_groupTV);
            String userString = getUsernamesFromTeam(team);
            users.setText(userString);
        }
        }

        private String getUsernamesFromTeam(Team team) {
            Set<User> userSet = team.getUsers();
            return userSet.stream()
                    .map(User::getUsername)
                    .collect(Collectors.joining(", "));
        }
    }
