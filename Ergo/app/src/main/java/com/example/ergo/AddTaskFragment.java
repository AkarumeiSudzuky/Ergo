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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ergo.model.Task;
import com.example.ergo.model.User;
import com.example.ergo.retrofit.RetrofitService;
import com.example.ergo.retrofit.TaskAPI;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

        calendar = Calendar.getInstance();

        selectedStartDate = Calendar.getInstance().getTime();
        selectedEndDate = Calendar.getInstance().getTime();

        updateDateDisplay(startDateTV, selectedStartDate);
        updateDateDisplay(endDateTV, selectedEndDate);


        startDateTV.setOnClickListener(v -> showDatePickerDialog(true));
        endDateTV.setOnClickListener(v -> showDatePickerDialog(false));

//        loadFriends(); // Load friends into the list
//        setupFriendSelection(view); // Set up click listeners

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
