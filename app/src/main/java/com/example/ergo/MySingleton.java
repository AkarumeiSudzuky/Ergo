package com.example.ergo;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MySingleton {
    private static MySingleton nInstance;
    private RequestQueue requestQueue;
    private static Context context;

    // Private constructor to prevent instantiation
    private MySingleton(Context ctx) {
        context = ctx.getApplicationContext();  // Use application context to avoid memory leaks
        requestQueue = getRequestQueue();
    }

    // Method to return the single instance of MySingleton
    public static synchronized MySingleton getInstance(Context ctx) {
        if (nInstance == null) {
            nInstance = new MySingleton(ctx);
        }
        return nInstance;
    }

    // Method to get the RequestQueue
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    // Method to add requests to the RequestQueue
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
