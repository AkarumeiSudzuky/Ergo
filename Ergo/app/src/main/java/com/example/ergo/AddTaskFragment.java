package com.example.ergo;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ergo.model.Friend;
import com.example.ergo.model.Task;
import com.example.ergo.retrofit.RetrofitService;
import com.example.ergo.retrofit.TaskAPI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AddTaskFragment extends Fragment {

    private EditText titleEditText, descriptionEditText;
    private Button saveTaskButton, StartDateButton, EndDateButton;
    private DatePickerDialog startDatePickerDialog, endDatePickerDialog;
    private Date selectedStartDate, selectedEndDate;

    private List<Friend> friends;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_task_fragment, container, false);

        titleEditText = view.findViewById(R.id.TitleET);
        StartDateButton = view.findViewById(R.id.StartDateButton);
        EndDateButton = view.findViewById(R.id.EndDateButton);
        descriptionEditText = view.findViewById(R.id.editTextTextMultiLine);
        saveTaskButton = view.findViewById(R.id.SaveTaskButton);

        selectedStartDate = Calendar.getInstance().getTime();
        selectedEndDate = Calendar.getInstance().getTime();
        StartDateButton.setText(getFormattedDate(selectedStartDate));
        EndDateButton.setText(getFormattedDate(selectedEndDate));

        initDatePickers();
        loadFriends(); // Load friends into the list
        setupFriendSelection(view); // Set up click listeners

        StartDateButton.setOnClickListener(v -> startDatePickerDialog.show());
        EndDateButton.setOnClickListener(v -> endDatePickerDialog.show());

        saveTaskButton.setOnClickListener(v -> performSavingTask());

        return view;
    }

    private void loadFriends() {
        //actual friends
        friends = new ArrayList<>();
        friends.add(new Friend("Alice"));
//        friends.add(new Friend("A"));
    }

    private void setupFriendSelection(View view) {
        LinearLayout friendsLayout = view.findViewById(R.id.Friends_to_add_to_task);

        for (Friend friend : friends) {
            View friendView = LayoutInflater.from(getContext()).inflate(R.layout.choose_friends_list_item, friendsLayout, false);
            TextView friendName = friendView.findViewById(R.id.Friend_item_name);
            Switch friendSwitch = friendView.findViewById(R.id.Friend_item_switch);

            friendName.setText(friend.getName());

            friendView.setOnClickListener(v -> {
                // Toggle the selected state
                friend.setSelected(!friend.isSelected());
                friendSwitch.setChecked(friend.isSelected());

                // Change the background based on selection
                if (friend.isSelected()) {
                    friendSwitch.setBackgroundResource(R.drawable.switch_clicked);
                } else {
                    friendSwitch.setBackgroundResource(R.drawable.switch_not_clicked);
                }
            });

            friendsLayout.addView(friendView);
        }
    }

    private void initDatePickers() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        startDatePickerDialog = new DatePickerDialog(requireActivity(),
                (view, year, month, dayOfMonth) -> {
                    month = month + 1;
                    selectedStartDate = createDate(year, month, dayOfMonth);
                    StartDateButton.setText(getFormattedDate(selectedStartDate));
                }, currentYear, currentMonth, currentDay);

        endDatePickerDialog = new DatePickerDialog(requireActivity(),
                (view, year, month, dayOfMonth) -> {
                    month = month + 1;
                    selectedEndDate = createDate(year, month, dayOfMonth);
                    EndDateButton.setText(getFormattedDate(selectedEndDate));
                }, currentYear, currentMonth, currentDay);

        startDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        endDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private void performSavingTask() {
        final String title = titleEditText.getText().toString().trim();
        final String description = descriptionEditText.getText().toString().trim();
        List<String> selectedFriendsNames = new ArrayList<>();

        if (title.isEmpty() || selectedStartDate == null || selectedEndDate == null || description.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        if (friends != null) {
            for (Friend friend : friends) {
                if (friend.isSelected()) {
                    selectedFriendsNames.add(friend.getName());
                }
            }
        } else {
            Toast.makeText(getActivity(), "No friends loaded", Toast.LENGTH_LONG).show();
        }


        RetrofitService retrofitService = new RetrofitService();
        TaskAPI taskAPI = retrofitService.getRetrofit().create(TaskAPI.class);

        Task task = new Task();
        task.setTitle(title);
        task.setStartDate(selectedStartDate);
        task.setStopDate(selectedEndDate);
        task.setDescription(description);
        //friend select
        task.setSelectedFriends(selectedFriendsNames);


        taskAPI.saveTask(task);
    }

    private Date createDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        return calendar.getTime();
    }

    private String getFormattedDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(date);
    }
}
