package com.example.ergo;

import android.content.DialogInterface;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ergo.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //==================For CRUD===============
    Button btn;
    EditText Name, Password, Email;
    String insert_user = "http://192.168.150.1/ergo/insert_user.php";
    String select_user = "http://192.168.150.1/ergo/select_user.php";
    AlertDialog.Builder builder;
    //============================================

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        setSupportActionBar(binding.toolbar);
//
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//
//        binding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAnchorView(R.id.fab)
//                        .setAction("Action", null).show();
//            }
//        });
//
//        //==================For CRUD register===============
//        setContentView(R.layout.register_screen);
//        btn = (Button)findViewById(R.id.CreateAccountButton);
//        Name = (EditText)findViewById(R.id.UsernameInput);
//        Password = (EditText)findViewById(R.id.PasswordInput);
//        Email = (EditText)findViewById(R.id.EmailInput);
//        builder = new AlertDialog.Builder(MainActivity.this);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String name, email, password;
//                name = Name.getText().toString();
//                email = Email.getText().toString();
//                password = Password.getText().toString();
//                StringRequest strRequest = new StringRequest(Request.Method.POST, insert_user,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                builder.setTitle("Server Response");
//                                builder.setMessage("Responsse: " + response);
//                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        Name.setText("");
//                                        Email.setText("");
//                                        Password.setText("");
//                                    }
//                                });
//                                AlertDialog alertDialog = builder.create();
//                                alertDialog.show();
//                            }
//                        },
//                        new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                String errorMessage = "Error: " + error.toString();
//
//                                // Log the full error response if available
//                                if (error.networkResponse != null) {
//                                    errorMessage += "\nResponse code: " + error.networkResponse.statusCode;
//                                    errorMessage += "\nData: " + new String(error.networkResponse.data);
//                                }
//
//                                // Print to console (Logcat)
//                                Log.e("MainActivity", errorMessage); // Tag your log for easier filtering
//
//                                // Show toast message
//                                Toast.makeText(MainActivity.this, "Error occurred, check log for details.", Toast.LENGTH_LONG).show();
//
//                            }
//                        }){
//                    @Override
//                    protected Map<String, String> getParams() throws AuthFailureError {
//                        Map<String, String> params = new HashMap<>();
//                        params.put("username", name);
//                        params.put("email", email);
//                        params.put("password", password);
//
//                        return params;
//                    }
//
//                };
//
//                MySingleton.getInstance(MainActivity.this).addToRequestQueue(strRequest);
//
//            }
//        });
//        //============================================




    //=======================Login============================

        setContentView(R.layout.login_screen);
        btn = (Button)findViewById(R.id.LogInButton);
        Name = (EditText)findViewById(R.id.EmailET);
        Password = (EditText)findViewById(R.id.PasswordET);
        builder = new AlertDialog.Builder(MainActivity.this);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = Name.getText().toString().trim();
                final String password = Password.getText().toString().trim();

                // Check if fields are empty
                if (name.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter username and password", Toast.LENGTH_LONG).show();
                    return;
                }

                StringRequest strRequest = new StringRequest(Request.Method.POST, select_user,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    // Parse the response as JSON
                                    JSONObject jsonResponse = new JSONObject(response);
                                    String status = jsonResponse.getString("status");
                                    String message = jsonResponse.getString("message");

                                    if (status.equals("success")) {
                                        // Successful login
                                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                                        // Clear fields or navigate to another activity
                                        Name.setText("");
                                        Password.setText("");
                                    } else {
                                        // Show error dialog
                                        builder.setTitle("Login Failed");
                                        builder.setMessage(message);
                                        builder.setPositiveButton("OK", null);
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(MainActivity.this, "JSON parsing error", Toast.LENGTH_LONG).show();
                                }
                            }

                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String errorMessage = "Error: " + error.toString();
                                Log.e("MainActivity", errorMessage);

                                if (error.networkResponse != null) {
                                    errorMessage += "\nResponse code: " + error.networkResponse.statusCode;
                                    errorMessage += "\nData: " + new String(error.networkResponse.data);
                                }

                                // Show toast message
                                Toast.makeText(MainActivity.this, "Error occurred, check log for details.", Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", name);
                        params.put("password", password);
                        return params;
                    }
                };

                // Add request to the queue
                MySingleton.getInstance(MainActivity.this).addToRequestQueue(strRequest);
            }
        });
        //============================================



    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
}