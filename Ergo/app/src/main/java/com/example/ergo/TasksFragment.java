package com.example.ergo;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ergo.model.Task;
import com.example.ergo.model.User;
import com.example.ergo.retrofit.RetrofitService;
import com.example.ergo.retrofit.TaskAPI;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
@SuppressWarnings("deprecation")
public class TasksFragment extends Fragment {
    private static final int FILE_PICKER_REQUEST_CODE = 0;
    private User user;
    private ImageButton logOutButton;
    private TextView importButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CurrentTasksFragment currentTasksFragment;
    private CompletedTasksFragment completedTasksFragment;

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
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        // Get the user object passed from the previous fragment or activity
        if (getArguments() != null) {
            user = (User) getArguments().getParcelable("user");
        }

        // Initialize TabLayout and ViewPager
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout.setupWithViewPager(viewPager);

        // Create instances of the fragments
        currentTasksFragment = new CurrentTasksFragment();
        completedTasksFragment = new CompletedTasksFragment();

        // Pass the user object to the TasksPagerAdapter
        TasksPagerAdapter tasksPageAdapter = new TasksPagerAdapter(getActivity().getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, user);
        tasksPageAdapter.addFragment(currentTasksFragment, "Current");
        tasksPageAdapter.addFragment(completedTasksFragment, "Completed");
        viewPager.setAdapter(tasksPageAdapter);

        logOutButton = view.findViewById(R.id.imageButton);
        logOutButton.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.logOut(); // Call the logOut method in MainActivity
            }
        });

        importButton = view.findViewById(R.id.importButton);
        importButton.setOnClickListener(v -> openFilePicker());

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

    public void updateTaskStatus(int taskId, int newStatus, List<Task> tasksDueToday, List<Task> tasksNotDue) {
        RetrofitService retrofitService = new RetrofitService();
        TaskAPI taskAPI = retrofitService.getRetrofit().create(TaskAPI.class);

        taskAPI.updateStatus(taskId, newStatus).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Task movedTask = null;

                    // Remove the task from the current lists and get the task instance
                    Iterator<Task> iterator = tasksDueToday.iterator();
                    while (iterator.hasNext()) {
                        Task task = iterator.next();
                        if (task.getId() == taskId) {
                            task.setStatus(newStatus);
                            movedTask = task;
                            iterator.remove();
                            break;
                        }
                    }
                    if (movedTask == null) {
                        iterator = tasksNotDue.iterator();
                        while (iterator.hasNext()) {
                            Task task = iterator.next();
                            if (task.getId() == taskId) {
                                task.setStatus(newStatus);
                                movedTask = task;
                                iterator.remove();
                                break;
                            }
                        }
                    }

                    // Update the CurrentTasksFragment to reflect the change
                    if (currentTasksFragment != null) {
                        currentTasksFragment.clearData();
                        currentTasksFragment.updateUI();
                    }

                    // Handle the status change and move task to CompletedTasksFragment or CurrentTasksFragment
                    if (newStatus == 3 && movedTask != null && completedTasksFragment != null) {
                        completedTasksFragment.addTask(movedTask); // Add to completed tasks
                        completedTasksFragment.updateUI();
                    } else if (newStatus != 3 && movedTask != null && currentTasksFragment != null) {
                        // Add back to current tasks (either due today or not due today)
                        if (movedTask.getStopDate().substring(0, 10).equals(String.valueOf(LocalDate.now()))) {
                            tasksDueToday.add(movedTask);
                        } else {
                            tasksNotDue.add(movedTask);
                        }
                        currentTasksFragment.updateUI(); // Ensure the UI is refreshed
                    }
                } else {
                    Log.e("TasksFragment", "Failed to update task status for task ID: " + taskId + ". Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Log.e("TasksFragment", "Error occurred while updating task status", throwable);
            }
        });
    }


    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select Task File"), FILE_PICKER_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                readTaskFile(uri);
            }
        }
    }


    private void readTaskFile(Uri uri) {
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
            String taskData = stringBuilder.toString();
            parseAndSaveTask(taskData);
        } catch (Exception e) {
            Log.e("TasksFragment", "Error reading task file", e);
            Toast.makeText(getActivity(), "Failed to read task file", Toast.LENGTH_SHORT).show();
        }
    }

    private void parseAndSaveTask(String taskData) {
        try {
            JSONObject jsonObject = new JSONObject(taskData);
            Task task = createTaskFromJson(jsonObject);
            Log.d("TasksFragment", "Task to save: " + task.toString());
            saveTaskToServer(task);
        } catch (JSONException e) {
            Log.e("TasksFragment", "Error parsing JSON", e);
            Toast.makeText(getActivity(), "Invalid task file format", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            Log.e("TasksFragment", "Error parsing dates", e);
            Toast.makeText(getActivity(), "Invalid date format in task file", Toast.LENGTH_SHORT).show();
        }
    }

    private Task createTaskFromJson(JSONObject jsonObject) throws JSONException, ParseException {
        String title = jsonObject.getString("title");
        String description = jsonObject.getString("description");
        String startDateStr = jsonObject.getString("startDate");
        String endDateStr = jsonObject.getString("stopDate");
        int priority = jsonObject.getInt("priority");
        int status = jsonObject.getInt("status");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
        Date startDate = dateFormat.parse(startDateStr);
        Date endDate = dateFormat.parse(endDateStr);

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStartDate(startDate);
        task.setStopDate(endDate);
        task.setPriority(priority);
        task.setStatus(status);
        task.setUser(user);
        return task;
    }

    private void saveTaskToServer(Task task) {
        RetrofitService retrofitService = new RetrofitService();
        TaskAPI taskAPI = retrofitService.getRetrofit().create(TaskAPI.class);
        taskAPI.saveTask(task).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Task imported successfully!", Toast.LENGTH_LONG).show();
                } else {
                    Log.e("TasksFragment", "Failed to import task. Response code: " + response.code());
                    Toast.makeText(getActivity(), "Failed to import task", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Log.e("TasksFragment", "Error occurred while importing task", throwable);
                Toast.makeText(getActivity(), "Error occurred while importing task", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
