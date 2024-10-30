package com.example.ergo;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ergo.model.Task;
import com.example.ergo.retrofit.RetrofitService;
import com.example.ergo.retrofit.TaskAPI;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTaskFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private EditText titleEditText, descriptionEditText;
    private Button saveTaskButton;
    private TextView startDateTV, endDateTV;
    private Date selectedStartDate, selectedEndDate;
    private Calendar calendar;

    private Spinner PrioritySpinner;
    private Spinner StatusSpinner;

//
//    private List<Friend> friends;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_task_fragment, container, false);

        titleEditText = view.findViewById(R.id.TitleET);
        startDateTV = view.findViewById(R.id.StartDateTextView);
        endDateTV = view.findViewById(R.id.EndDateTextView);
        descriptionEditText = view.findViewById(R.id.editTextTextMultiLine);
        saveTaskButton = view.findViewById(R.id.SaveTaskButton);

        //spinners
        StatusSpinner = view.findViewById(R.id.StatusSpinner);
        PrioritySpinner = view.findViewById(R.id.PrioritySpinner);

        //status
        String[] spinnerStatusItems = getResources().getStringArray(R.array.spinner_status_items);
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, spinnerStatusItems);
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        StatusSpinner.setAdapter(adapterStatus);

        StatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = spinnerStatusItems[position];

                if (position == 0) {
                    StatusSpinner.setBackgroundResource(R.drawable.status_and_priority_red);
                } else if (position == 1) {
                    StatusSpinner.setBackgroundResource(R.drawable.status_and_priority_yellow);
                } else if (position == 2) {
                    StatusSpinner.setBackgroundResource(R.drawable.status_and_priority_green);
                }
                //Toast.makeText(requireContext(), "Selected item: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //priority
        String[] spinnerPriorityItems = getResources().getStringArray(R.array.spinner_priority_items);
        ArrayAdapter<String> adapterPriority = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, spinnerPriorityItems);
        adapterPriority.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        PrioritySpinner.setAdapter(adapterPriority);

        PrioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = spinnerPriorityItems[position];
                if (position == 0) {
                    PrioritySpinner.setBackgroundResource(R.drawable.status_and_priority_green);
                } else if (position == 1) {
                    PrioritySpinner.setBackgroundResource(R.drawable.status_and_priority_yellow);
                } else if (position == 2) {
                    PrioritySpinner.setBackgroundResource(R.drawable.status_and_priority_red);
                }
                //Toast.makeText(requireContext(), "Selected item: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //calendar
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
//        List<String> selectedFriendsNames = new ArrayList<>();

        if (title.isEmpty() || selectedStartDate == null || selectedEndDate == null || description.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_LONG).show();
            return;
        }

//        if (friends != null) {
//            for (Friend friend : friends) {
//                if (friend.isSelected()) {
//                    selectedFriendsNames.add(friend.getName());
//                }
//            }
//        } else {
//            Toast.makeText(getActivity(), "No friends loaded", Toast.LENGTH_LONG).show();
//        }

        RetrofitService retrofitService = new RetrofitService();
        TaskAPI taskAPI = retrofitService.getRetrofit().create(TaskAPI.class);

        Task task = new Task();
        task.setTitle(title);
        task.setStartDate(selectedStartDate);
        task.setStopDate(selectedEndDate);
        task.setDescription(description);
//        //friend select
//        task.setSelectedFriends(selectedFriendsNames);

        taskAPI.saveTask(task);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
