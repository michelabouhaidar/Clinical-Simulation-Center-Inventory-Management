package com.example.session;

public final class AppSession {

    private static LoggedInUser currentUser;

    private AppSession() {}

    public static void setCurrentUser(LoggedInUser user) {
        currentUser = user;
    }

    public static LoggedInUser getCurrentUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
