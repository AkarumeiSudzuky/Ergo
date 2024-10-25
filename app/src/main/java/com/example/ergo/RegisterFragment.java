package com.example.ergo;

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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {
    private EditText usernameEditText, passwordEditText, emailEditText;
    private Button createAccountButton;
    private String insertUserUrl = "http://192.168.150.1/ergo/insert_user.php";
    private AlertDialog.Builder builder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment, container, false);
        usernameEditText = view.findViewById(R.id.UsernameInput);
        passwordEditText = view.findViewById(R.id.PasswordInput);
        emailEditText = view.findViewById(R.id.EmailInput);
        createAccountButton = view.findViewById(R.id.CreateAccountButton);
        builder = new AlertDialog.Builder(getActivity());

        TextView logInText = view.findViewById(R.id.LogInText);
        logInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).loadFragment(new LoginFragment());
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

        // Check for empty fields
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        StringRequest strRequest = new StringRequest(Request.Method.POST, insertUserUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");

                            // Handle server response
                            if (status.equals("success")) {
                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                // Navigate to TasksFragment
                                ((MainActivity) getActivity()).loadFragment(new TasksFragment());
                            } else {
                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error parsing response", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //temporarily:
                        ((MainActivity) getActivity()).loadFragment(new TasksFragment());
                        //

                        String errorMessage = "Error: " + error.toString();
                        Log.e("RegisterFragment", errorMessage);
                        Toast.makeText(getActivity(), "Error occurred, check log for details.", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        MySingleton.getInstance(getActivity()).addToRequestQueue(strRequest);
    }
}
