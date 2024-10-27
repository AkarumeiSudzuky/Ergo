package com.example.ergo;

import static android.widget.Toast.LENGTH_LONG;

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

public class LoginFragment extends Fragment {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        emailEditText = view.findViewById(R.id.EmailET);
        passwordEditText = view.findViewById(R.id.PasswordET);
        loginButton = view.findViewById(R.id.SaveTaskButton);

        TextView noAccountYet = view.findViewById(R.id.RegisterText);
        noAccountYet.setOnClickListener(v -> ((MainActivity) getActivity()).loadFragment(new RegisterFragment()));


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        return view;
    }


    public void performLogin() {
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        RetrofitService retrofitService = new RetrofitService();
        UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);

        // Check if email and password fields are empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Email and Password cannot be empty!", LENGTH_LONG).show();
            return;
        }

        userAPI.getUserByUsername(email)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            User user = response.body();
                            // Check if the provided password matches the one from the server
                            if (user.getPassword().equals(password)) {
                                ((MainActivity) getActivity()).onLoginSuccess();
                                ((MainActivity) getActivity()).loadFragment(new TasksFragment());
                            } else {
                                Toast.makeText(getActivity(), "Invalid password!", LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "User not found!", LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable throwable) {
                        Toast.makeText(getActivity(),"Login failed!", LENGTH_LONG).show();
                        Logger.getLogger(LoginFragment.class.getName()).log(Level.SEVERE,"Error occurred",throwable);
                    }
                });
    }



//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        loginButton = view.findViewById(R.id.SaveTaskButton);
//        loginButton.setOnClickListener(v -> {
//
//
//            ((MainActivity) getActivity()).onLoginSuccess();
//        });
//    }

//    private void performLogin() {
//        final String email = emailEditText.getText().toString().trim();
//        final String password = passwordEditText.getText().toString().trim();
//
//        if (email.isEmpty() || password.isEmpty()) {
//            Toast.makeText(getActivity(), "Please enter email and password", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        StringRequest strRequest = new StringRequest(Request.Method.POST, selectUserUrl,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonResponse = new JSONObject(response);
//                            String status = jsonResponse.getString("status");
//                            String message = jsonResponse.getString("message");
//
//                            if (status.equals("success")) {
//                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
//                                emailEditText.setText("");
//                                passwordEditText.setText("");
//                                // Handle successful login (e.g., navigate to another activity)
//
//                                ((MainActivity) getActivity()).loadFragment(new TasksFragment());
//                            } else {
//                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Toast.makeText(getActivity(), "JSON parsing error", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        //temporarily:
//                        ((MainActivity) getActivity()).loadFragment(new TasksFragment());
//                        //
//
//                        Log.e("LoginFragment", "Error: " + error.toString());
//                        Toast.makeText(getActivity(), "Error occurred, check log for details.", Toast.LENGTH_LONG).show();
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("username", email); // Assuming username is the email
//                params.put("password", password);
//                return params;
//            }
//        };
//
//        MySingleton.getInstance(getActivity()).addToRequestQueue(strRequest);
//    }
}
