package com.example.vietnamhistoryapplication.utils;

import com.example.vietnamhistoryapplication.models.UserModel;

public class UserSession {
    private static UserModel currentUser;

    public static void setCurrentUser(UserModel user) {
        currentUser = user;
    }

    public static UserModel getCurrentUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }
}
