package com.example.ergo;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.ergo.model.Task;
import com.example.ergo.model.Team;
import com.example.ergo.model.User;
import com.example.ergo.retrofit.RetrofitService;
import com.example.ergo.retrofit.TaskAPI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("deprecation")
public class CurrentTasksFragment extends TasksFragment {
    private User user;

    private ListView tasksListViewDueToday;
    private ListView tasksListViewNotDue;
    private List<Task> tasksDueToday = new ArrayList<>();
    private List<Task> tasksNotDue = new ArrayList<>();
    private List<Task> completedTasks = new ArrayList<>();
    private List<Task> notCompletedTasks = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            MainActivity activity = (MainActivity) context;
            activity.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.current_tasks_fragment, container, false);

        if (getArguments() != null) {
            user = (User) getArguments().getParcelable("user");
        }

        tasksListViewDueToday = view.findViewById(R.id.TasksListViewDueTodayCurrent);
        tasksListViewNotDue = view.findViewById(R.id.TaskListViewNotDueCurrent);

        fetchTasks();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        clearData();
        fetchTasks();
    }

    private void fetchTasks() {
        if (!tasksDueToday.isEmpty() || !tasksNotDue.isEmpty()) {
            updateUI();
            return;
        }

        RetrofitService retrofitService = new RetrofitService();
        TaskAPI taskAPI = retrofitService.getRetrofit().create(TaskAPI.class);

        Long userId = user.getId();
        taskAPI.getTasksForUser (userId).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<Task> allTasks = response.body();
                        sortTasksByProgress(allTasks);
                        sortTasksByDate(notCompletedTasks);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void clearData(){
        notCompletedTasks.clear();
        tasksNotDue.clear();
        tasksDueToday.clear();
    }

    private void sortTasksByDate(List<Task> allTasks) {
        String today = String.valueOf(LocalDate.now());

        for (Task task : allTasks) {
            if (task.getStopDate().substring(0, 10).equals(today) && !containsTaskWithId(tasksDueToday, task.getId())) {
                tasksDueToday.add(task);
            } else if (!containsTaskWithId(tasksNotDue, task.getId()) &&!containsTaskWithId(tasksDueToday, task.getId()) ) {
                tasksNotDue.add(task);
            }

        }

        CurrentTasksFragment.TaskAdapter dueTodayAdapter = new CurrentTasksFragment.TaskAdapter(getContext(), tasksDueToday, user);
        tasksListViewDueToday.setAdapter(dueTodayAdapter);
        setListViewHeightBasedOnChildren(tasksListViewDueToday);


        CurrentTasksFragment.TaskAdapter notDueAdapter = new CurrentTasksFragment.TaskAdapter(getContext(), tasksNotDue, user);
        tasksListViewNotDue.setAdapter(notDueAdapter);
        setListViewHeightBasedOnChildren(tasksListViewNotDue);
    }

    private boolean containsTaskWithId(List<Task> taskList, long taskId) {
        for (Task existingTask : taskList) {
            if (existingTask.getId() == taskId) {
                return true;
            }
        }
        return false;
    }

    private void sortTasksByProgress(List<Task> allTasks){
        for (Task task : allTasks) {
            if (task.getStatus() == 3 && !containsTaskWithId(completedTasks, task.getId())) {
                completedTasks.add(task);
            } else if(!containsTaskWithId(notCompletedTasks, task.getId()) && task.getStatus() != 3) {
                notCompletedTasks.add(task);
            }
        }
    }


    public void updateUI() {
        TaskAdapter dueTodayAdapter = new TaskAdapter(getContext(), tasksDueToday, user);
        tasksListViewDueToday.setAdapter(dueTodayAdapter);
        setListViewHeightBasedOnChildren(tasksListViewDueToday);

        TaskAdapter notDueAdapter = new TaskAdapter(getContext(), tasksNotDue, user);
        tasksListViewNotDue.setAdapter(notDueAdapter);
        setListViewHeightBasedOnChildren(tasksListViewNotDue);
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


            Team team = task.getTeam();

            if (team == null || team.getId() == null) {
                teamIcon.setVisibility(View.GONE);
            } else {
                teamIcon.setVisibility(View.VISIBLE);
            }


            boolean[] isCompleted = {task.getStatus() == 3};

            // Set initial image
            completionButton.setBackgroundResource(isCompleted[0] ? R.drawable.checkbox_on : R.drawable.checkbox_off);

            completionButton.setOnClickListener(v -> {
                isCompleted[0] = !isCompleted[0];
                completionButton.setBackgroundResource(isCompleted[0] ? R.drawable.checkbox_on : R.drawable.checkbox_off);

                // Update task status here
                if (isCompleted[0]) {
                    updateTaskStatus(task.getId(), 3, tasksDueToday, tasksNotDue);
                    // Move task from notCompletedTasks to completedTasks
                    notCompletedTasks.remove(task);
                    completedTasks.add(task);
                    ((ArrayAdapter) tasksListViewDueToday.getAdapter()).notifyDataSetChanged();
                    ((ArrayAdapter) tasksListViewNotDue.getAdapter()).notifyDataSetChanged();
                    updateUI();
                } else {
                    updateTaskStatus(task.getId(), 2,tasksDueToday,tasksNotDue);

                    completedTasks.remove(task);
                    notCompletedTasks.add(task);
                    ((ArrayAdapter) tasksListViewDueToday.getAdapter()).notifyDataSetChanged();
                    ((ArrayAdapter) tasksListViewNotDue.getAdapter()).notifyDataSetChanged();
                    updateUI();
                }
            });


            String priority = "";
            String status = "";

            switch (task.getPriority()) {
                case 3:
                    priority = "Severe";
                    taskPriority.setTextColor(ContextCompat.getColor(context, R.color.status_red));
                    break;
                case 2:
                    taskPriority.setTextColor(ContextCompat.getColor(context, R.color.status_yellow));
                    priority = "Medium";
                    break;
                case 1:
                    taskPriority.setTextColor(ContextCompat.getColor(context, R.color.status_green));
                    priority = "Light";
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

            convertView.setOnLongClickListener(v -> {
                showDeleteConfirmationDialog(task, position);
                return true;
            });

            convertView.setOnClickListener(v -> {
                Task selectedTask = tasks.get(position);
                // Navigate to TaskDetailsFragment
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                TaskDetailsFragment taskDetailsFragment = new TaskDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("task", selectedTask); // Pass the task object
                taskDetailsFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, taskDetailsFragment) // Replace with your container ID
                        .addToBackStack(null)
                        .commit();
            });

            return convertView;
        }

        private void showDeleteConfirmationDialog(Task task, int position) {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Delete", (dialog, which) -> deleteTask(task, position))
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        private void deleteTask(Task task, int position) {
            RetrofitService retrofitService = new RetrofitService();
            TaskAPI taskAPI = retrofitService.getRetrofit().create(TaskAPI.class);

            taskAPI.deleteTask(task.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                        tasks.remove(position);
                        notifyDataSetChanged();
                        updateUI();
                    } else {
                        Toast.makeText(context, "Failed to delete task: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Error deleting task: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
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
