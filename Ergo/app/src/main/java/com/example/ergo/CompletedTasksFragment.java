package com.example.ergo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.ergo.model.Task;
import com.example.ergo.model.User;
import com.example.ergo.retrofit.RetrofitService;
import com.example.ergo.retrofit.TaskAPI;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompletedTasksFragment extends Fragment {
    private User user;


    private ListView tasksListViewDueToday;
    private ListView tasksListViewNotDue;
    private List<Task> tasksDueToday = new ArrayList<>();
    private List<Task> tasksNotDue = new ArrayList<>();
    private List<Task> completedTasks = new ArrayList<>();
    private List<Task> notCompletedTasks = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.completed_tasks_fragment, container, false);
//        Log.d("user_info", user.toString());
        // Get the user object passed from the previous fragment or activity
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }
        if (user != null) {
            Toast.makeText(getActivity(), "User ID: " + user.getId(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "User not found!", Toast.LENGTH_LONG).show();
        }

        // Initialize ListViews
        tasksListViewDueToday = view.findViewById(R.id.TasksListViewDueTodayCompleted);
        tasksListViewNotDue = view.findViewById(R.id.TaskListViewNotDueCompleted);

        // Fetch tasks from the server
        fetchTasks();

        return view;
    }

    private void fetchTasks() {
        RetrofitService retrofitService = new RetrofitService();
        TaskAPI taskAPI = retrofitService.getRetrofit().create(TaskAPI.class);

        Long userId = user.getId();
        Log.d("user_id", userId.toString());


        taskAPI.getTasksForUser(userId).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<Task> allTasks = response.body();
                        //!
                        sortTasksByDate(allTasks);
                    } else {
                        showToast("No tasks found in the response body.");
                    }
                } else {
                    showToast("Failed to fetch tasks. HTTP Status: " + response.code());
                    Log.e("TasksFragment", "Error response: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                showToast("Error: " + t.getMessage());
            }
        });
    }

    private void sortTasksByDate(List<Task> allTasks) {
        String today = String.valueOf(LocalDate.now());  // Get today's date

        // Separate tasks into "Due Today" and "Not Due"
        for (Task task : allTasks) {
            if (task.getStopDate().substring(0, 10).equals(today)) {
                tasksDueToday.add(task);
            } else {
                tasksNotDue.add(task);
            }
        }

        // Set the adapter for tasks due today
        CompletedTasksFragment.TaskAdapter dueTodayAdapter = new CompletedTasksFragment.TaskAdapter(getContext(), tasksDueToday, user);
        tasksListViewDueToday.setAdapter(dueTodayAdapter);
        setListViewHeightBasedOnChildren(tasksListViewDueToday); // Adjust height based on content

        // Set the adapter for tasks not due today
        CompletedTasksFragment.TaskAdapter notDueAdapter = new CompletedTasksFragment.TaskAdapter(getContext(), tasksNotDue, user);
        tasksListViewNotDue.setAdapter(notDueAdapter);
        setListViewHeightBasedOnChildren(tasksListViewNotDue); // Adjust height based on content
    }

    private void sortTasksByProgress(List<Task> allTasks){
        for (Task task : allTasks) {
            if (task.getStatus() == 1) {
                completedTasks.add(task);
            } else {
                notCompletedTasks.add(task);
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private class TaskAdapter extends ArrayAdapter<Task> {
        private Context context;
        private List<Task> tasks;
        private User currentUser;

        public TaskAdapter(Context context, List<Task> tasks, User currentUser) {
            super(context, R.layout.task_list_item, tasks);
            this.context = context;
            this.tasks = tasks;
            this.currentUser = currentUser;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.task_list_item, parent, false);
            }

            Task task = tasks.get(position);

            TextView taskTitle = convertView.findViewById(R.id.TaskTitle_in_list);
            TextView taskPriority = convertView.findViewById(R.id.PriorityLabel_in_task);
            TextView taskStatus = convertView.findViewById(R.id.StatusLabel_in_task);
            TextView taskTimeRange = convertView.findViewById(R.id.TaskTimeRange_in_list);
            Button completionButton = convertView.findViewById(R.id.completedButton);
            View teamIcon = convertView.findViewById(R.id.teamIcon);

            //changed here!!!!!!!!!!!! visibility of team icon   remove! here to check
//            if (currentUser != null && task.getUser() != null && !task.getUser().getId().equals(currentUser.getId())) {
//                teamIcon.setVisibility(View.VISIBLE);
//            } else {
//                teamIcon.setVisibility(View.GONE);
//            }

            boolean[] isCompleted = {task.getStatus() == 3}; // Assume status 3 means completed; adjust if needed

            // Set initial image
            completionButton.setBackgroundResource(isCompleted[0] ? R.drawable.checkbox_on : R.drawable.checkbox_off);

            completionButton.setOnClickListener(v -> {
                isCompleted[0] = !isCompleted[0];
                completionButton.setBackgroundResource(isCompleted[0] ? R.drawable.checkbox_on : R.drawable.checkbox_off);

                // Update task status here
            });


            String priority = "";
            String status = "";

            switch (task.getPriority()) {
                case 3:
                    priority = "Light";
                    taskPriority.setTextColor(ContextCompat.getColor(context, R.color.status_green));
                    break;
                case 2:
                    taskPriority.setTextColor(ContextCompat.getColor(context, R.color.status_yellow));
                    priority = "Medium";
                    break;
                case 1:
                    taskPriority.setTextColor(ContextCompat.getColor(context, R.color.status_red));
                    priority = "Severe";
                    break;
            }
            switch (task.getStatus()) {
                case 1:
                    taskStatus.setTextColor(ContextCompat.getColor(context, R.color.status_red));
                    status = "Not Started";
                    break;
                case 2:
                    taskStatus.setTextColor(ContextCompat.getColor(context, R.color.status_yellow));
                    status = "In Progress";
                    break;
                case 3:
                    taskStatus.setTextColor(ContextCompat.getColor(context, R.color.status_green));
                    status = "Completed";
                    break;
            }

            taskTitle.setText(task.getTitle());
            taskPriority.setText(priority);
            taskStatus.setText(status);
            taskTimeRange.setText(formatIsoDate(task.getStopDate()));

            return convertView;
        }

        public String formatIsoDate(String isoDate) {
            try {
                if (isoDate == null || isoDate.isEmpty()) {
                    return "Invalid date";
                }

                LocalDate date;
                if (isoDate.contains("T")) {
                    date = LocalDate.parse(isoDate, DateTimeFormatter.ISO_DATE_TIME);
                } else {
                    date = LocalDate.parse(isoDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }

                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM dd", Locale.getDefault());
                return date.format(outputFormatter);
            } catch (DateTimeParseException e) {
                Log.e("CompletedTasksFragment", "Failed to parse date: " + isoDate, e);
                return "Invalid date";
            }
        }
    }


    private void performCompleteTask(boolean isCompleted) {
        if (isCompleted) {

        }
    }


    private void setListViewHeightBasedOnChildren(ListView listView) {
        ArrayAdapter adapter = (ArrayAdapter) listView.getAdapter();
        if (adapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(
                    View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.UNSPECIFIED
            );
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
