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

        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        String usernamePattern = "^[a-zA-Z0-9]{3,15}$"; // Username: 3-15 numbers and/or characters
        String passwordPattern = "^(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])(?=.*[a-zA-Z]).{8,}$"; // Password: At least 8 characters in total: 1 number, 1 special character

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            makeText(getActivity(), "Please fill in all fields", LENGTH_LONG).show();
            return;
        }

        if (!email.matches(emailPattern)) {
            makeText(getActivity(), "Invalid email format", LENGTH_LONG).show();
            return;
        }

        if (!username.matches(usernamePattern)) {
            makeText(getActivity(), "Username must be 3-15 alphanumeric characters", LENGTH_LONG).show();
            return;
        }

        if (!password.matches(passwordPattern)) {
            makeText(getActivity(), "Password must be at least 8 characters, including 1 number and 1 special character", LENGTH_LONG).show();
            return;
        }

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername(username);
        signUpRequest.setEmail(email);
        signUpRequest.setPassword(password);
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        RetrofitService retrofitService = new RetrofitService();
        UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);

        userAPI.signup(signUpRequest)
                .enqueue(new Callback<JwtAuthenticationResponse>() {
                    @Override
                    public void onResponse(Call<JwtAuthenticationResponse> call, Response<JwtAuthenticationResponse> response) {
                        if (response.isSuccessful()) {
                            JwtAuthenticationResponse authResponse = response.body();
                            String token = authResponse.getToken();

                            // Assuming MainActivity has a method to handle login success
                            ((MainActivity) getActivity()).onLoginSuccess(user);

                            Toast.makeText(getActivity(), "Registration successful!", LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "Registration failed!", LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JwtAuthenticationResponse> call, Throwable throwable) {
                        Toast.makeText(getActivity(), "Registration failed!", LENGTH_LONG).show();
                        Logger.getLogger(RegisterFragment.class.getName()).log(Level.SEVERE, "Error occurred", throwable);
                    }
                });
    }

}