package com.example.ergo;

import static android.widget.Toast.LENGTH_LONG;

import android.app.DatePickerDialog;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ergo.model.Task;
import com.example.ergo.model.User;
import com.example.ergo.retrofit.RetrofitService;
import com.example.ergo.retrofit.TaskAPI;
import com.example.ergo.retrofit.UserAPI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTaskFragment extends Fragment {

    private EditText titleEditText, descriptionEditText;
    private Button saveTaskButton;
    private TextView startDateTV, endDateTV;
    private Date selectedStartDate, selectedEndDate;
    private Calendar calendar;

    private User user;
    private Spinner PrioritySpinner;
    private Spinner StatusSpinner;


    //searching for users
    private SearchView userSearchView;
    private ListView userListView;
    private ArrayAdapter<User> userAdapter;
    private List<User> userList = new ArrayList<>();

    //undertasks
    private LinearLayout elementsContainer;
    private Button addElementButton;

//
//    private List<Friend> friends;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_task_fragment, container, false);


        // Retrieve the User object from the arguments
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }
//
//        if (user != null) {
//            Toast.makeText(getActivity(), "Welcome " + user.getUsername(), Toast.LENGTH_LONG).show();
//        }


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



        elementsContainer = view.findViewById(R.id.elements_container);
        addElementButton = view.findViewById(R.id.add_element_button);
        addElementButton.setOnClickListener(v -> addElement());



        saveTaskButton.setOnClickListener(v -> performSavingTask());

        return view;
    }

//    private void loadFriends() {
//        //actual friends
////        friends.add(new Friend("A"));
//    }

//
//    private void setupFriendSelection(View view) {
//        LinearLayout friendsLayout = view.findViewById(R.id.Friends_to_add_to_task);
//
//        for (Friend friend : friends) {
//            View friendView = LayoutInflater.from(getContext()).inflate(R.layout.choose_friends_list_item, friendsLayout, false);
//            TextView friendName = friendView.findViewById(R.id.Friend_item_name);
//            Switch friendSwitch = friendView.findViewById(R.id.Friend_item_switch);
//
//            friendName.setText(friend.getName());
//
//            friendView.setOnClickListener(v -> {
//                // Toggle the selected state
//                friend.setSelected(!friend.isSelected());
//                friendSwitch.setChecked(friend.isSelected());
//
//                // Change the background based on selection
//                if (friend.isSelected()) {
//                    friendSwitch.setBackgroundResource(R.drawable.switch_clicked);
//                } else {
//                    friendSwitch.setBackgroundResource(R.drawable.switch_not_clicked);
//                }
//            });
//
//            friendsLayout.addView(friendView);
//        }
//    }

    private void addElement() {
        View elementView = LayoutInflater.from(getActivity()).inflate(R.layout.dynamic_undertask,
                elementsContainer, false);

        Button elementButton = elementView.findViewById(R.id.element_button);
        TextView elementTitle = elementView.findViewById(R.id.element_title);

        elementButton.setBackgroundResource(R.drawable.switch_not_clicked);

        // Button click listener to toggle completion
        elementButton.setOnClickListener(v -> {
            boolean isCompleted = (elementButton.getTag() != null) && (boolean) elementButton.getTag();

            if (isCompleted) {
                // If already completed, mark as not completed
                elementButton.setBackgroundResource(R.drawable.switch_not_clicked);
                elementButton.setTag(false); // Mark it as not completed
                Toast.makeText(getActivity(), "Task marked as not completed!", Toast.LENGTH_SHORT).show();
            } else {
                // If not completed, mark as completed
                elementButton.setBackgroundResource(R.drawable.switch_clicked);
                elementButton.setTag(true); // Mark it as completed
                Toast.makeText(getActivity(), "Task marked as completed!", Toast.LENGTH_SHORT).show();
            }

        });

        elementsContainer.addView(elementView);
    }

    // Method to load all users (you will implement API call here)
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
                        statusSpinner.setBackgroundResource(R.drawable.status_and_priority_red);
                        break;
                    case 1:
                        statusSpinner.setBackgroundResource(R.drawable.status_and_priority_yellow);
                        break;
                    case 2:
                        statusSpinner.setBackgroundResource(R.drawable.status_and_priority_green);
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
                        prioritySpinner.setBackgroundResource(R.drawable.status_and_priority_green);
                        break;
                    case 1:
                        prioritySpinner.setBackgroundResource(R.drawable.status_and_priority_yellow);
                        break;
                    case 2:
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
        task.setUser(user);
        task.setStatus(0);
        task.setPriority(1);

        // Log the Task object to debug the data being sent
        Log.d("Task Data", task.toString());



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
