package com.example.bdtry.Data;

public class Login {
    private static int userId;
    private static String username;
    private static String role;

    public static void startSession(int id, String user, String userRole) {
        userId = id;
        username = user;
        role = userRole;
    }

    public static int getUserId() { return userId; }
    public static String getUsername() { return username; }
    public static String getRole() { return role; }

    public static void clearSession() {
        userId = 0;
        username = null;
        role = null;
    }
}

enum Role {
    ADMIN, SISWA, GURU, WALI_KELAS
}
