package com.fangxiong.Utils;

import com.fangxiong.dto.LoginResult;
import com.fangxiong.pojo.CurrentUserInfo;

public class CurrentHolder {
    //ThreadLocal线程Thread的局部变量，每个线程都有自己的一个CURRENT_LOCAL变量
    //CURRENT_LOCAL 是一个静态的 ThreadLocal 实例，它在类加载时被初始化。
    private static final ThreadLocal<LoginResult> CURRENT_LOCAL = new ThreadLocal<>();

    public static void setCurrentUser(LoginResult loginResult) {
        CURRENT_LOCAL.set(loginResult);
    }

    public static LoginResult getCurrentUserInfo() {
        return CURRENT_LOCAL.get();
    }

    public static void remove() {
        CURRENT_LOCAL.remove();
    }
}
