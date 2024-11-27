package com.example.ergo;

import static android.widget.Toast.LENGTH_LONG;

import android.os.Bundle;
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
import com.example.ergo.model.SignInRequest;
import com.example.ergo.model.User;
import com.example.ergo.retrofit.RetrofitService;
import com.example.ergo.retrofit.UserAPI;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private User activeUser;
    private RetrofitService retrofitService = new RetrofitService();
    private UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        emailEditText = view.findViewById(R.id.EmailET);
        passwordEditText = view.findViewById(R.id.TitleET);
        loginButton = view.findViewById(R.id.SaveTaskButton);

        TextView noAccountYet = view.findViewById(R.id.RegisterText);
        noAccountYet.setOnClickListener(v -> ((MainActivity) getActivity()).loadFragment(new RegisterFragment(), activeUser));

        loginButton.setOnClickListener(v -> performLogin());

        return view;
    }

    public void performLogin() {
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        // Check if email and password fields are empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Email and Password cannot be empty!", LENGTH_LONG).show();
            return;
        }

        // Create SignInRequest object
        SignInRequest signInRequest = new SignInRequest(email, password);

        userAPI.signin(signInRequest).enqueue(new Callback<JwtAuthenticationResponse>() {
            @Override
            public void onResponse(Call<JwtAuthenticationResponse> call, Response<JwtAuthenticationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    getUserByToken(token, new UserCallback() {
                        @Override
                        public void onSuccess(User user) {
                            activeUser = user;
                            ((MainActivity) getActivity()).onLoginSuccess(activeUser);
                        }
                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(getActivity(), errorMessage, LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Invalid email or password!", LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JwtAuthenticationResponse> call, Throwable throwable) {
                Toast.makeText(getActivity(), "Login failed!", LENGTH_LONG).show();
            }
        });
    }

    private void getUserByToken(String token, UserCallback callback) {
        userAPI.getCurrentUser("Bearer " + token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("API error: User wasn't fetched");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                callback.onFailure("Network error: " + throwable.getMessage());
            }
        });
    }
}