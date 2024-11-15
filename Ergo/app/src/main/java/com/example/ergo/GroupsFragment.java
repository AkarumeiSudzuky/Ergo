package com.example.ergo;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ergo.model.Team;
import com.example.ergo.model.User;
import com.example.ergo.retrofit.RetrofitService;
import com.example.ergo.retrofit.UserAPI;
import com.google.android.material.color.utilities.Contrast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupsFragment extends Fragment {
    private User user;

    private Button addNewGroupButton;
    private SearchView userSearchView;
    private ListView groupsListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.groups_fragment, container, false);

        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }

        addNewGroupButton = view.findViewById(R.id.AddNewGroupButton);
        groupsListView = view.findViewById(R.id.groupsListView);



        addNewGroupButton.setOnClickListener(v -> ((MainActivity) getActivity()).loadFragment(new AddNewGroupFragment(), user));

//        fetchGroupsList();

        return view;
    }

    //na przyszlosc
    private void fetchGroupsList() {

    }


    //nie zrobione, na przyszlosc
//    public class GroupsAdapter extends ArrayAdapter<User> {
//        private Context context;
//        private List<Team> groups;
//
//        public GroupsAdapter(Context context, List<Team> groups) {
//            super(context, R.layout.group_list_item, groups);
//            this.context = context;
//            this.groups = groups;
//        }
//
//        @NonNull
//        @Override
//        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//            if (convertView == null) {
//                convertView = LayoutInflater.from(context).inflate(R.layout.group_list_item, parent, false);
//            }
//
//            Team team = groups.get(position);
//
//
//            //add movement to new fragment with group info
//            //convertView.setOnClickListener(v -> );
//
//            return convertView;
//        }
//    }

}
