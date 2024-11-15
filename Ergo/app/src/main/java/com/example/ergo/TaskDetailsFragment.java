package com.example.ergo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.ergo.model.Task;
import com.example.ergo.model.User;


public class TaskDetailsFragment extends Fragment {
    private Task task;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_display_fragment, container, false);

        // Retrieve the Task object from the arguments
        if (getArguments() != null) {
            task = (Task) getArguments().getSerializable("task");
            user = (User) getArguments().getSerializable("user");
        }

//        // Populate your views with task details
//        TextView titleTextView = view.findViewById(R.id.TitleText);
//        titleTextView.setText(task.getTitle());
//        // Populate other fields similarly...

        return view;
    }
}