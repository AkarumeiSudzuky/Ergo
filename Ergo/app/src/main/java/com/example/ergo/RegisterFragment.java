package com.example.ergo;

import static android.widget.Toast.*;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.ergo.model.User;
import com.example.ergo.retrofit.RetrofitService;
import com.example.ergo.retrofit.UserAPI;


import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {
    private EditText usernameEditText, passwordEditText, emailEditText;
    private Button createAccountButton;
    private User activeUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment, container, false);
        usernameEditText = view.findViewById(R.id.UsernameInput);
        passwordEditText = view.findViewById(R.id.PasswordInput);
        emailEditText = view.findViewById(R.id.EmailInput);
        createAccountButton = view.findViewById(R.id.CreateAccountButton);

        TextView logInText = view.findViewById(R.id.LogInText);
        logInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).loadFragment(new LoginFragment(), activeUser);
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegister();
            }
        });

        return view;
    }

    private void performRegister() {
        final String username = usernameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();
        RetrofitService retrofitService = new RetrofitService();
        UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);

        // Check for empty fields
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            makeText(getActivity(), "Please fill in all fields", LENGTH_LONG).show();
            return;
        }

        createAccountButton.setOnClickListener(view->{
            activeUser.setUsername(username);
            activeUser.setEmail(email);
            activeUser.setPassword(password);
            userAPI.saveUser(activeUser)
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
//                            Toast.makeText(getActivity(),"Save successfull!", LENGTH_LONG).show();
                            ((MainActivity) getActivity()).onLoginSuccess(activeUser);

                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable throwable) {
                            Toast.makeText(getActivity(),"Save failed!", LENGTH_LONG).show();
                            Logger.getLogger(RegisterFragment.class.getName()).log(Level.SEVERE,"Error occurred",throwable);
                        }
                    });
        });



    }
}
