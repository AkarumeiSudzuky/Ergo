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


import com.example.ergo.model.JwtAuthenticationResponse;
import com.example.ergo.model.SignUpRequest;
import com.example.ergo.model.User;
import com.example.ergo.retrofit.RetrofitService;
import com.example.ergo.retrofit.UserAPI;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {
    private EditText usernameEditText, passwordEditText, emailEditText;
    private Button createAccountButton;
    private User activeUser;
    private RetrofitService retrofitService = new RetrofitService();
    private UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);

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

    protected void performRegister() {
        final String username = usernameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if (checkFields(username, email, password)) {
            checkUsernameUnique(username, isUnique -> {
                if (isUnique) {
                    SignUpRequest signUpRequest = new SignUpRequest();
                    signUpRequest.setUsername(username);
                    signUpRequest.setEmail(email);
                    signUpRequest.setPassword(password);

                    userAPI.signup(signUpRequest).enqueue(new Callback<JwtAuthenticationResponse>() {
                        @Override
                        public void onResponse(Call<JwtAuthenticationResponse> call, Response<JwtAuthenticationResponse> response) {
                            if (response.isSuccessful()) {
                                JwtAuthenticationResponse authResponse = response.body();
                                ((MainActivity) getActivity()).onLoginSuccess(new User(username, email, password));
                                Toast.makeText(getActivity(), "Registration successful!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "Registration failed!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<JwtAuthenticationResponse> call, Throwable throwable) {
                            Toast.makeText(getActivity(), "Registration failed!", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "This username is taken. Choose a different one.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    private boolean checkFields(String username, String email, String password){
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        String usernamePattern = "^[a-zA-Z0-9]{3,15}$"; // Username: 3-15 numbers and/or characters
        String passwordPattern = "^(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])(?=.*[a-zA-Z]).{8,}$"; // Password: At least 8 characters in total: 1 number, 1 special character

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            makeText(getActivity(), "Please fill in all fields", LENGTH_LONG).show();
            return false;
        }

        if (!email.matches(emailPattern)) {
            makeText(getActivity(), "Invalid email format", LENGTH_LONG).show();
            return false;
        }

        if (!username.matches(usernamePattern)) {
            makeText(getActivity(), "Username must be 3-15 alphanumeric characters", LENGTH_LONG).show();
            return false;
        }

        if (!password.matches(passwordPattern)) {
            makeText(getActivity(), "Password must be at least 8 characters, including 1 number and 1 special character", LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    private void checkUsernameUnique(String username, UsernameCheckCallback callback) {
        userAPI.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean isUnique = response.body().stream()
                            .noneMatch(user -> user.getUsername().equals(username));
                    callback.onResult(isUnique);  // Pass result to callback
                } else {
                    callback.onResult(false);  // Default to not unique if API fails
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                callback.onResult(false);  // Treat as not unique on failure
            }
        });
    }


}