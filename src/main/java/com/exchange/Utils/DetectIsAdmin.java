package com.exchange.Utils;

public class DetectIsAdmin {
    public static boolean isAdmin() {
        Integer role = CurrentHolder.getCurrentUserInfo().getRole();
        return role != null && role == 2;
    }
}
