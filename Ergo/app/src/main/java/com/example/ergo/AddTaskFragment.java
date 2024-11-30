package com.example.ergo;


import android.app.DatePickerDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ergo.model.Task;
import com.example.ergo.model.Team;
import com.example.ergo.model.User;
import com.example.ergo.retrofit.RetrofitService;
import com.example.ergo.retrofit.TaskAPI;
import com.example.ergo.retrofit.UserAPI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("deprecation")
public class AddTaskFragment extends Fragment {

    private EditText titleEditText, descriptionEditText;
    private Button saveTaskButton;
    private TextView startDateTV, endDateTV;
    private Date selectedStartDate, selectedEndDate;
    private int priority, status;
    private Calendar calendar;

    private User user;
    private User friend;
    private Team team;
    private Spinner PrioritySpinner;
    private Spinner StatusSpinner;


    //searching for users
    private SearchView userSearchView;
    private ListView userListView;
    private ArrayAdapter<Map<String, Object>> userAdapter;
    private List<Map<String, Object>> userList = new ArrayList<>();
    private List<User> friendsList = new ArrayList<>();
    private List<Map<String, Object>> filteredList = new ArrayList<>();

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_task_fragment, container, false);


        // Retrieve the User object from the arguments
        if (getArguments() != null) {
            user = (User) getArguments().getParcelable("user");
            team = (Team) getArguments().getSerializable("team");
        }

        titleEditText = view.findViewById(R.id.TitleET);
        startDateTV = view.findViewById(R.id.StartDateTextView);
        endDateTV = view.findViewById(R.id.EndDateTextView);
        descriptionEditText = view.findViewById(R.id.editTextTextMultiLine);
        saveTaskButton = view.findViewById(R.id.SaveTaskButton);
        userSearchView = view.findViewById(R.id.userSearchView);
        userListView = view.findViewById(R.id.userListView);

        //spinners
        StatusSpinner = view.findViewById(R.id.StatusSpinner);
        PrioritySpinner = view.findViewById(R.id.PrioritySpinner);

        setupStatusSpinner(StatusSpinner);
        setupPrioritySpinner(PrioritySpinner);

        calendar = Calendar.getInstance();

        selectedStartDate = Calendar.getInstance().getTime();
        selectedEndDate = Calendar.getInstance().getTime();
        updateDateDisplay(startDateTV, selectedStartDate);
        updateDateDisplay(endDateTV, selectedEndDate);
        startDateTV.setOnClickListener(v -> showDatePickerDialog(true));
        endDateTV.setOnClickListener(v -> showDatePickerDialog(false));


        userSearchView = view.findViewById(R.id.userSearchView);
        userListView = view.findViewById(R.id.userListView);


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


        // Set up the adapter for displaying all users
        userAdapter = new ArrayAdapter<Map<String, Object>>(getActivity(), android.R.layout.simple_list_item_1, userList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                // Inflate the view if it does not exist
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                }

                // Get the current user map
                Map<String, Object> currentUser = getItem(position);
                String username = (String) currentUser.get("username");

                // Set the username in the TextView
                TextView textView = convertView.findViewById(android.R.id.text1);
                textView.setText(username);

                return convertView;
            }
        };

        userListView.setAdapter(userAdapter);

        // Set up search view to filter users by name
        userSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    filteredList.clear();
                    userAdapter.clear();
                    userAdapter.notifyDataSetChanged();
                    userListView.setVisibility(View.GONE);
                } else {
                    filterFriends(newText);
                }
                return true;
            }
        });

        // Set up the item click listener for the user list
        userListView.setOnItemClickListener((parent, itemView, position, id) -> {
            // Get the selected user's map
            Map<String, Object> selectedUserMap = userAdapter.getItem(position);

            // Extract the user's ID and username
            Long userId = (Long) selectedUserMap.get("id");
            String username = (String) selectedUserMap.get("username");

            // Find the User object from the friends list based on the ID
            for (User friend : friendsList) {
                if (friend.getId() == userId) {
                    this.friend = friend; // Assign the selected friend to the `friend` variable
                    break;
                }
            }
            userSearchView.setQuery(username, false);
            filteredList.clear();
            userAdapter.clear();
            userAdapter.notifyDataSetChanged();
            userListView.setVisibility(View.GONE);

        });

        loadFriends();



        saveTaskButton.setOnClickListener(v -> performSavingTask());

        return view;
    }

    private void loadFriends(){
        UserAPI userAPI = new RetrofitService().getRetrofit().create(UserAPI.class);
        userAPI.getAllFriends(user.getId()).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                friendsList = response.body();
//                Log.d("Friends", friendsList.toString());
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable throwable) {
                Toast.makeText(getActivity(),"Fail to fetch friends",Toast.LENGTH_LONG);
            }
        });
    }




    // Method to filter users locally
    private void filterFriends(String query) {

        for (User friend : friendsList) {
            if (friend.getUsername().toLowerCase().contains(query.toLowerCase())) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", friend.getId());
                userMap.put("username", friend.getUsername());
                filteredList.add(userMap);
                userListView.setVisibility(View.VISIBLE);
            }
        }
//        Log.d("Filter", "Filtered list size: " + filteredList.size());

        userAdapter.clear();
        userAdapter.addAll(filteredList);
        userAdapter.notifyDataSetChanged();
    }

    private void setupStatusSpinner(Spinner statusSpinner) {
        String[] spinnerStatusItems = getResources().getStringArray(R.array.spinner_status_items);
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, spinnerStatusItems);
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapterStatus);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = spinnerStatusItems[position];
                switch (position) {
                    case 0:
                        status = 1;
                        statusSpinner.setBackgroundResource(R.drawable.status_and_priority_red);
                        break;
                    case 1:
                        status = 2;
                        statusSpinner.setBackgroundResource(R.drawable.status_and_priority_yellow);
                        break;
                    case 2:
                        status = 3;
                        statusSpinner.setBackgroundResource(R.drawable.status_and_priority_green);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setupPrioritySpinner(Spinner prioritySpinner) {
        String[] spinnerPriorityItems = getResources().getStringArray(R.array.spinner_priority_items);
        ArrayAdapter<String> adapterPriority = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, spinnerPriorityItems);
        adapterPriority.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapterPriority);

        prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = spinnerPriorityItems[position];
                switch (position) {
                    case 0:
                        priority =1;
                        prioritySpinner.setBackgroundResource(R.drawable.status_and_priority_green);
                        break;
                    case 1:
                        priority =2;
                        prioritySpinner.setBackgroundResource(R.drawable.status_and_priority_yellow);
                        break;
                    case 2:
                        priority=3;
                        prioritySpinner.setBackgroundResource(R.drawable.status_and_priority_red);
                        break;
                }
                // Optionally show the selected item as a Toast
                // Toast.makeText(requireContext(), "Selected item: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Optional: handle no selection if needed
            }
        });
    }


    private void showDatePickerDialog(boolean isStartDate) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    Date selectedDate = calendar.getTime();
                    if (isStartDate) {
                        selectedStartDate = selectedDate;
                        updateDateDisplay(startDateTV, selectedStartDate);
                    } else {
                        selectedEndDate = selectedDate;
                        updateDateDisplay(endDateTV, selectedEndDate);
                    }
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void updateDateDisplay(TextView textView, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        textView.setText(dateFormat.format(date));
    }


    private void performSavingTask() {
        final String title = titleEditText.getText().toString().trim();
        final String description = descriptionEditText.getText().toString().trim();

        RetrofitService retrofitService = new RetrofitService();
        TaskAPI taskAPI = retrofitService.getRetrofit().create(TaskAPI.class);

        if (title.isEmpty() || selectedStartDate == null || selectedEndDate == null || description.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_LONG).show();
            return;
        }


        Task task = new Task();
        task.setTitle(title);
        task.setStartDate(selectedStartDate);
        task.setStopDate(selectedEndDate);
        task.setDescription(description);
        task.setStatus(status);
        task.setPriority(priority);

        if (friend != null){
            task.setUser(friend);
        }
        else {
            task.setUser(user);
        }

        if (team != null && team.getId()!= null && team.getId() != -1) {
            //needed change here to correctly set team.
            task.setTeam(team);
        } else {
            task.setTeam(null);
        }


        task.setStartDate(selectedStartDate);
        task.setStopDate(selectedEndDate);


        taskAPI.saveTask(task).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("API Response", "Code: " + response.code());
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Save successful!", Toast.LENGTH_LONG).show();
                } else {
                    // Log error response
                    Log.e("API Error", "Response code: " + response.code() + ", message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Log.e("API Failure", "Error occurred", throwable);
                Toast.makeText(getActivity(), "Save failed!", Toast.LENGTH_LONG).show();
            }
        });


    }
}