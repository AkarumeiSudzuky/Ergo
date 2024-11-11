package com.example.ergo;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ergo.model.Task;
import com.example.ergo.model.User;
import com.example.ergo.retrofit.RetrofitService;
import com.example.ergo.retrofit.TaskAPI;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TasksFragment extends Fragment {
    private User user;
    private ImageButton logOutButton;
    private ListView tasksListViewDueToday;
    private ListView tasksListViewNotDue;
    private List<Task> tasksDueToday = new ArrayList<>();
    private List<Task> tasksNotDue = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        // Get the user object passed from the previous fragment or activity
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }

        // Initialize ListViews
        tasksListViewDueToday = view.findViewById(R.id.TasksListViewDueToday);
        tasksListViewNotDue = view.findViewById(R.id.TaskListViewNotDue);
        logOutButton = view.findViewById(R.id.imageButton);

        logOutButton.setOnClickListener(v -> {
            // Ensure the activity is MainActivity
            if (getActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.logOut(); // Call the logOut method in MainActivity
            }
        });

        // Fetch tasks from the server
        fetchTasks();

        return view;
    }

    private void fetchTasks() {
        RetrofitService retrofitService = new RetrofitService();
        TaskAPI taskAPI = retrofitService.getRetrofit().create(TaskAPI.class);

        // Fetch tasks for the current user
        Call<List<Task>> call = taskAPI.getTasksForUser(user.getId());
        call.enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<Task> allTasks = response.body();
                        separateTasksByDate(allTasks);  // Process tasks if the body is not null
                    } else {
                        showToast("No tasks found in the response body.");
                    }
                } else {
                    // Log and show the status code in case of a failure response
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

    private void separateTasksByDate(List<Task> allTasks) {
        String today = getTodayDate();  // Get today's date

        // Separate tasks into "Due Today" and "Not Due"
        for (Task task : allTasks) {
            if (task.getStopDate().substring(0, 10).equals(today)) {
                tasksDueToday.add(task);
            } else {
                tasksNotDue.add(task);
            }
        }

        // Set the adapter for tasks due today
        TaskAdapter dueTodayAdapter = new TaskAdapter(getContext(), tasksDueToday);
        tasksListViewDueToday.setAdapter(dueTodayAdapter);
        setListViewHeightBasedOnChildren(tasksListViewDueToday); // Adjust height based on content

        // Set the adapter for tasks not due today
        TaskAdapter notDueAdapter = new TaskAdapter(getContext(), tasksNotDue);
        tasksListViewNotDue.setAdapter(notDueAdapter);
        setListViewHeightBasedOnChildren(tasksListViewNotDue); // Adjust height based on content
    }


    private String getTodayDate() {
        // This can be replaced with dynamic date logic
        return "2024-11-11";  // Example: hardcoded for now
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Adapter for displaying tasks in ListViews
    private class TaskAdapter extends ArrayAdapter<Task> {
        private Context context;
        private List<Task> tasks;

        public TaskAdapter(Context context, List<Task> tasks) {
            super(context, R.layout.task_list_item, tasks);
            this.context = context;
            this.tasks = tasks;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.task_list_item, parent, false);
            }

            Task task = tasks.get(position);

            // Bind task data to the view elements
            TextView taskTitle = convertView.findViewById(R.id.TaskTitle_in_list);
            TextView taskPriority = convertView.findViewById(R.id.PriorityLabel_in_task);
            TextView taskStatus = convertView.findViewById(R.id.StatusLabel_in_task);
            TextView taskTimeRange = convertView.findViewById(R.id.TaskTimeRange_in_list);

            String priority = "";
            String status = "";

            switch (task.getPriority()) {
                case 1:
                    priority = "Not Started";
                    //taskStatus.setTextColor(R.color.status_yellow);
                    break;
                case 2:
                    priority = "In Progress";
                    break;
                case 3:
                    priority = "Completed";
                    break;
            }
            switch (task.getStatus()) {
                case 1:
                    status = "Light";
                    break;
                case 2:
                    status = "Medium";
                    break;
                case 3:
                    status = "Severe";
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

                // Example if isoDate is in the form "2024-11-11" (date-only format)
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(isoDate, inputFormatter);

                // Format the date to "MMMM dd"
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM dd", Locale.getDefault());
                return date.format(outputFormatter);
            } catch (DateTimeParseException e) {
                Log.e("TasksFragment", "Failed to parse date: " + isoDate, e);
                return "Invalid date";
            }
        }



    }


    private void setListViewHeightBasedOnChildren(ListView listView) {
        ArrayAdapter adapter = (ArrayAdapter) listView.getAdapter();
        if (adapter == null) {
            // No adapter is assigned yet
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
