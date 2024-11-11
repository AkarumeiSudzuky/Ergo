package com.example.ergo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ergo.model.User;

public class UndertaskFragment extends Fragment {
    private User user;
    private LinearLayout elementsContainer;
    private Button addElementButton;
    private Button saveUndertasksButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.undertask_fragment, container, false);

        // Retrieve the User object from the arguments
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }

        elementsContainer = view.findViewById(R.id.elements_container_in_fragment);
        addElementButton = view.findViewById(R.id.add_element_button_in_fragment);
        //adding more undertasks
        addElementButton.setOnClickListener(v -> addElement());

        saveUndertasksButton = view.findViewById(R.id.SaveUndertasksButton);
        saveUndertasksButton.setOnClickListener(v -> performSaveUndertasks());

        return view;
    }

    private void performSaveUndertasks() {
        //needed return to AddTaskFragment and transportation of data from here to AddTaskFragment.
        //

    }

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
                elementButton.setTag(false);
                Toast.makeText(getActivity(), "Task marked as not completed!", Toast.LENGTH_SHORT).show();
            } else {
                // If not completed, mark as completed
                elementButton.setBackgroundResource(R.drawable.switch_clicked);
                elementButton.setTag(true);
                Toast.makeText(getActivity(), "Task marked as completed!", Toast.LENGTH_SHORT).show();
            }

        });

        elementsContainer.addView(elementView);
    }

}
