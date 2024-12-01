package com.example.ergo;

import com.example.ergo.model.User;

public interface UserCallback {
    void onSuccess(User user);
    void onFailure(String errorMessage);
}
